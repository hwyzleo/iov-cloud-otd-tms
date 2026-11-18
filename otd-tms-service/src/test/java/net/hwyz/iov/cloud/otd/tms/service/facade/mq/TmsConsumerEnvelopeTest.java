package net.hwyz.iov.cloud.otd.tms.service.facade.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportOrderAppService;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * 入站消费者信封校验测试（OTD-TMS-DSN-CR-001 §1.7）
 *
 * @author hwyz_leo
 */
@DisplayName("TMS 入站消费者 - 信封校验")
class TmsConsumerEnvelopeTest {

    private static final String APPLIED_TOPIC = "vso.transport.applied";
    private static final String CHANGED_TOPIC = "wms.vehicle-outbound.changed";

    private final TransportOrderAppService appService = mock(TransportOrderAppService.class);
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    private final TmsProperties properties = new TmsProperties();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private VsoTransportAppliedConsumer appliedConsumer() {
        return new VsoTransportAppliedConsumer(appService, properties, objectMapper, kafkaTemplate);
    }

    private WmsOutboundFactConsumer factConsumer() {
        return new WmsOutboundFactConsumer(appService, properties, objectMapper, kafkaTemplate);
    }

    @Test
    @DisplayName("发运申请：合法信封委派建单")
    void shouldDelegateValidTransportApplied() {
        String json = """
                {"eventId":"EVT-1","eventType":"vso.transport.applied","schemaVersion":1,
                 "occurredAt":"2026-11-18T13:05:27.412+08:00","producer":"otd-vso","aggregateVersion":1,
                 "payload":{"orderNo":"SO-1","vin":"VIN1","deliveryStoreCode":"D0001","deliveryRegionCode":"R01"}}
                """;

        appliedConsumer().onTransportApplied(new ConsumerRecord<>(APPLIED_TOPIC, 0, 0, "SO-1", json));

        verify(appService).acceptTransportApplied(eq("EVT-1"), eq(APPLIED_TOPIC), anyString(),
                eq(new TransportOrderAppService.TransportAppliedMessage("SO-1", "VIN1", "D0001", "R01")));
    }

    @Test
    @DisplayName("发运申请：schemaVersion 非 1 或时间非 ISO 时进死信")
    void shouldDeadLetterInvalidAppliedEnvelope() {
        String wrongVersion = """
                {"eventId":"EVT-2","eventType":"vso.transport.applied","schemaVersion":2,
                 "occurredAt":"2026-11-18T13:05:27.412+08:00","payload":{"orderNo":"SO-1","vin":"VIN1"}}
                """;
        appliedConsumer().onTransportApplied(new ConsumerRecord<>(APPLIED_TOPIC, 0, 0, "SO-1", wrongVersion));
        verify(appService, never()).acceptTransportApplied(any(), any(), any(), any());
        verify(kafkaTemplate).send(eq("vso.transport.applied.tms.dlq"), eq("SO-1"), eq(wrongVersion));

        String epochTime = """
                {"eventId":"EVT-3","eventType":"vso.transport.applied","schemaVersion":1,
                 "occurredAt":1763441727412,"payload":{"orderNo":"SO-1","vin":"VIN1"}}
                """;
        appliedConsumer().onTransportApplied(new ConsumerRecord<>(APPLIED_TOPIC, 0, 0, "SO-1", epochTime));
        verify(appService, never()).acceptTransportApplied(any(), any(), any(), any());
    }

    @Test
    @DisplayName("出库事实：schemaVersion=2 且 ISO 时间时委派更新")
    void shouldDelegateValidOutboundFact() {
        String json = """
                {"eventId":"EVT-4","eventType":"VehicleOutboundChangedEvent","schemaVersion":2,
                 "occurredAt":"2026-11-18T13:05:27.412+08:00","aggregateVersion":3,
                 "payload":{"outboundOrderNum":"OB-1","sourceOrderNum":"TO-1",
                            "items":[{"vin":"VIN1","state":"OUTBOUND"}]}}
                """;

        factConsumer().onOutboundFact(new ConsumerRecord<>(CHANGED_TOPIC, 0, 0, "TO-1", json));

        verify(appService).applyOutboundFact(eq("EVT-4"), eq(CHANGED_TOPIC), anyString(), eq("TO-1"),
                eq("OB-1"), eq(java.util.List.of(new TransportOrderAppService.WmsOutboundItem("VIN1", "OUTBOUND"))));
    }

    @Test
    @DisplayName("出库事实：旧 schemaVersion=1 进死信")
    void shouldDeadLetterLegacyOutboundFact() {
        String json = """
                {"eventId":"EVT-5","eventType":"VehicleOutboundChangedEvent","schemaVersion":1,
                 "occurredAtEpochMillis":1763441727412,"payload":{"sourceOrderNum":"TO-1","items":[]}}
                """;

        factConsumer().onOutboundFact(new ConsumerRecord<>(CHANGED_TOPIC, 0, 0, "TO-1", json));

        verify(appService, never()).applyOutboundFact(any(), any(), any(), any(), any(), any());
        verify(kafkaTemplate).send(eq("wms.vehicle-outbound.changed.tms.dlq"), eq("TO-1"), eq(json));
    }

    @Test
    @DisplayName("出库拒绝事实：委派标记受阻")
    void shouldDelegateRejection() {
        String json = """
                {"eventId":"EVT-6","eventType":"VehicleOutboundCommandRejectedEvent","schemaVersion":2,
                 "occurredAt":"2026-11-18T13:05:27.412+08:00",
                 "payload":{"sourceOrderNum":"TO-1","reasonCode":"302003","itemReasons":[]}}
                """;

        factConsumer().onOutboundFact(new ConsumerRecord<>("wms.vehicle-outbound.command-rejected",
                0, 0, "TO-1", json));

        verify(appService).applyOutboundRejection(eq("EVT-6"), eq("wms.vehicle-outbound.command-rejected"),
                anyString(), eq("TO-1"), eq("302003"));
    }
}
