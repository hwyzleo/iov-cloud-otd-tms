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
import java.util.ArrayList;
import java.util.List;

/**
 * WMS 出库事实消费者（OTD-TMS-DSN-CR-001 §1.5）
 * <p>
 * 消费 {@code wms.vehicle-outbound.changed} 与 {@code wms.vehicle-outbound.command-rejected}：
 * 校验 schemaVersion=2 与 ISO-8601 时间，按来源单号（运单号）更新明细事实或标记出库受阻。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tms.consumer.auto-startup", havingValue = "true", matchIfMissing = true)
public class WmsOutboundFactConsumer {

    private static final int EXPECTED_SCHEMA_VERSION = 2;
    private static final String EVENT_CHANGED = "VehicleOutboundChangedEvent";
    private static final String EVENT_REJECTED = "VehicleOutboundCommandRejectedEvent";

    private final TransportOrderAppService transportOrderAppService;
    private final TmsProperties properties;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 消费出库事实
     *
     * @param record Kafka 记录
     */
    @KafkaListener(
            topics = {
                    "${tms.topics.wms-outbound-changed:wms.vehicle-outbound.changed}",
                    "${tms.topics.wms-outbound-command-rejected:wms.vehicle-outbound.command-rejected}"
            },
            groupId = "${tms.consumer.outbound-fact-group:otd-tms-outbound-fact}"
    )
    public void onOutboundFact(ConsumerRecord<String, String> record) {
        String payloadDigest = sha256(record.value());
        JsonNode root;
        try {
            root = objectMapper.readTree(record.value());
        } catch (Exception e) {
            deadLetter(record, payloadDigest, "消息不是合法 JSON");
            return;
        }
        String eventId = text(root, "eventId");
        String eventType = text(root, "eventType");
        Integer schemaVersion = root.hasNonNull("schemaVersion") ? root.get("schemaVersion").asInt() : null;
        String occurredAt = text(root, "occurredAt");
        if (eventId == null || eventId.isBlank() || schemaVersion == null
                || schemaVersion != EXPECTED_SCHEMA_VERSION
                || occurredAt == null || !isIsoInstant(occurredAt)) {
            deadLetter(record, payloadDigest, "事件信封或 schemaVersion 非法");
            return;
        }
        JsonNode payload = root.path("payload");
        String sourceOrderNum = text(payload, "sourceOrderNum");
        if (EVENT_CHANGED.equals(eventType)) {
            transportOrderAppService.applyOutboundFact(eventId, record.topic(), payloadDigest, sourceOrderNum,
                    text(payload, "outboundOrderNum"), items(payload));
            return;
        }
        if (EVENT_REJECTED.equals(eventType)) {
            transportOrderAppService.applyOutboundRejection(eventId, record.topic(), payloadDigest, sourceOrderNum,
                    text(payload, "reasonCode"));
            return;
        }
        deadLetter(record, payloadDigest, "未知事件类型: " + eventType);
    }

    private List<TransportOrderAppService.WmsOutboundItem> items(JsonNode payload) {
        JsonNode items = payload.get("items");
        if (items == null || !items.isArray()) {
            return List.of();
        }
        List<TransportOrderAppService.WmsOutboundItem> result = new ArrayList<>(items.size());
        items.forEach(item -> result.add(new TransportOrderAppService.WmsOutboundItem(
                text(item, "vin"), text(item, "state"))));
        return result;
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
        log.error("出库事实非法[{}]，转死信: key={}, reason={}", payloadDigest, record.key(), reason);
        try {
            kafkaTemplate.send(properties.getTopics().getWmsOutboundChangedDlq(), record.key(), record.value());
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
