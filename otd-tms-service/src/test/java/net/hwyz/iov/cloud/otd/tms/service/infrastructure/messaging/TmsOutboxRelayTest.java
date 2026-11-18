package net.hwyz.iov.cloud.otd.tms.service.infrastructure.messaging;

import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsOutboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TMS 出站中继测试（OTD-TMS-DSN-CR-001 §1.7）
 *
 * @author hwyz_leo
 */
@DisplayName("TMS 出站中继")
class TmsOutboxRelayTest {

    private final TmsOutboxRepository repository = mock(TmsOutboxRepository.class);
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    private final TmsProperties properties = new TmsProperties();

    private TmsOutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new TmsOutboxRelay(repository, kafkaTemplate, properties);
    }

    private OutboxRecord record(Integer retryCount) {
        return OutboxRecord.builder()
                .id(1L)
                .eventId("EVT-1")
                .eventType("VehicleOutboundRequested")
                .aggregateType("TRANSPORT_ORDER")
                .aggregateId("TO-1")
                .aggregateVersion(1L)
                .topic("wms.vehicle-outbound.requested")
                .messageKey("TO-1")
                .payload("{\"eventId\":\"EVT-1\"}")
                .publishState("PENDING")
                .retryCount(retryCount)
                .build();
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<SendResult<String, String>> success() {
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        return future;
    }

    @Test
    @DisplayName("Broker 确认后置 PUBLISHED")
    void shouldMarkPublishedOnSuccess() {
        when(repository.listPublishable(any(Date.class), anyInt())).thenReturn(List.of(record(0)));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(success());

        relay.relayMessages();

        verify(kafkaTemplate).send("wms.vehicle-outbound.requested", "TO-1", "{\"eventId\":\"EVT-1\"}");
        verify(repository).markPublished(1L);
    }

    @Test
    @DisplayName("发布失败安排退避重试")
    void shouldScheduleRetry() {
        when(repository.listPublishable(any(Date.class), anyInt())).thenReturn(List.of(record(0)));
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failed);

        long before = System.currentTimeMillis();
        relay.relayMessages();

        ArgumentCaptor<Date> nextRetry = ArgumentCaptor.forClass(Date.class);
        verify(repository).markRetryable(eq(1L), eq(1), nextRetry.capture(), anyString());
        assertThat(nextRetry.getValue().getTime()).isGreaterThanOrEqualTo(before + 5000L);
        verify(repository, never()).markDead(anyLong(), anyString());
    }

    @Test
    @DisplayName("超过重试上限置 DEAD")
    void shouldMarkDead() {
        when(repository.listPublishable(any(Date.class), anyInt()))
                .thenReturn(List.of(record(properties.getOutbox().getRelay().getMaxRetry() - 1)));
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failed);

        relay.relayMessages();

        verify(repository).markDead(eq(1L), anyString());
        verify(repository, never()).markPublished(anyLong());
    }
}
