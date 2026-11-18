package net.hwyz.iov.cloud.otd.tms.service.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsOutboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TMS 出站消息中继（OTD-TMS-DSN-CR-001）
 * <p>
 * 按有界批次扫描 PENDING／FAILED_RETRYABLE，使用不可变载荷与注册 Topic／Key 发布；
 * 仅在 Broker 确认后置 PUBLISHED，超过重试上限置 DEAD 并告警。不参与业务事务。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tms.outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class TmsOutboxRelay {

    private static final int MAX_ERROR_LENGTH = 2000;

    private final TmsOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TmsProperties properties;

    /**
     * 定时扫描并发布
     */
    @Scheduled(fixedDelayString = "${tms.outbox.relay.scan-interval-ms:10000}")
    public void relayMessages() {
        List<OutboxRecord> records = outboxRepository.listPublishable(new Date(),
                properties.getOutbox().getRelay().getBatchSize());
        if (records.isEmpty()) {
            return;
        }
        log.info("TMS 出站中继扫描到 {} 条待发布记录", records.size());
        for (OutboxRecord record : records) {
            relayOne(record);
        }
    }

    private void relayOne(OutboxRecord record) {
        try {
            kafkaTemplate.send(record.getTopic(), record.getMessageKey(), record.getPayload())
                    .get(30, TimeUnit.SECONDS);
            outboxRepository.markPublished(record.getId());
            log.info("出站事件发布成功: eventId={}, topic={}, key={}", record.getEventId(), record.getTopic(),
                    record.getMessageKey());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            handleFailure(record, e);
        } catch (Exception e) {
            handleFailure(record, e);
        }
    }

    private void handleFailure(OutboxRecord record, Exception e) {
        int retryCount = record.getRetryCount() == null ? 1 : record.getRetryCount() + 1;
        String error = truncate(e.getMessage());
        TmsProperties.Relay relay = properties.getOutbox().getRelay();
        if (retryCount >= relay.getMaxRetry()) {
            outboxRepository.markDead(record.getId(), error);
            log.error("出站事件置 DEAD，需人工处置: eventId={}, error={}", record.getEventId(), error);
            return;
        }
        long backoff = Math.min(relay.getBaseBackoffMs() << Math.min(retryCount - 1, 16), relay.getMaxBackoffMs());
        Date nextRetryTime = new Date(System.currentTimeMillis() + backoff);
        outboxRepository.markRetryable(record.getId(), retryCount, nextRetryTime, error);
        log.warn("出站事件发布失败，安排重试: eventId={}, retryCount={}, nextRetryTime={}",
                record.getEventId(), retryCount, nextRetryTime);
    }

    private String truncate(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH - 3) + "...";
    }
}
