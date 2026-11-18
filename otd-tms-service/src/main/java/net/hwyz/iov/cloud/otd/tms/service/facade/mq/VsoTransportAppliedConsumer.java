package net.hwyz.iov.cloud.otd.tms.service.facade.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportOrderAppService;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

/**
 * VSO 发运申请消费者（OTD-TMS-DSN-CR-001 §1.5）
 * <p>
 * 消费 {@code vso.transport.applied}：校验信封（schemaVersion=1、occurredAt 为 ISO-8601），
 * 幂等建单；非法信封转发至 TMS 专属死信 Topic。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tms.consumer.auto-startup", havingValue = "true", matchIfMissing = true)
public class VsoTransportAppliedConsumer {

    private static final int EXPECTED_SCHEMA_VERSION = 1;
    private static final String EXPECTED_EVENT_TYPE = "vso.transport.applied";

    private final TransportOrderAppService transportOrderAppService;
    private final TmsProperties properties;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 消费发运申请事实
     *
     * @param record Kafka 记录
     */
    @KafkaListener(
            topics = "${tms.topics.vso-transport-applied:vso.transport.applied}",
            groupId = "${tms.consumer.transport-applied-group:otd-tms-transport-applied}"
    )
    public void onTransportApplied(ConsumerRecord<String, String> record) {
        String payloadDigest = sha256(record.value());
        JsonNode root = readTree(record, payloadDigest);
        if (root == null) {
            return;
        }
        String eventId = text(root, "eventId");
        String eventType = text(root, "eventType");
        Integer schemaVersion = root.hasNonNull("schemaVersion") ? root.get("schemaVersion").asInt() : null;
        String occurredAt = text(root, "occurredAt");
        if (eventId == null || eventId.isBlank() || !EXPECTED_EVENT_TYPE.equals(eventType)
                || schemaVersion == null || schemaVersion != EXPECTED_SCHEMA_VERSION
                || occurredAt == null || !isIsoInstant(occurredAt)) {
            deadLetter(record, payloadDigest, "事件信封或 schemaVersion 非法");
            return;
        }
        JsonNode payload = root.path("payload");
        transportOrderAppService.acceptTransportApplied(eventId, record.topic(), payloadDigest,
                new TransportOrderAppService.TransportAppliedMessage(
                        text(payload, "orderNo"),
                        text(payload, "vin"),
                        text(payload, "deliveryStoreCode"),
                        text(payload, "deliveryRegionCode")));
    }

    private JsonNode readTree(ConsumerRecord<String, String> record, String payloadDigest) {
        try {
            return objectMapper.readTree(record.value());
        } catch (Exception e) {
            deadLetter(record, payloadDigest, "消息不是合法 JSON");
            return null;
        }
    }

    private boolean isIsoInstant(String text) {
        try {
            Instant.parse(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void deadLetter(ConsumerRecord<String, String> record, String payloadDigest, String reason) {
        log.error("发运申请事件非法[{}]，转死信: key={}, reason={}", payloadDigest, record.key(), reason);
        try {
            kafkaTemplate.send(properties.getTopics().getVsoTransportAppliedDlq(), record.key(), record.value());
        } catch (Exception e) {
            log.error("死信转发失败: {}", e.getMessage(), e);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value == null ? new byte[0] : value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
