package net.hwyz.iov.cloud.otd.tms.service.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwyz.iov.cloud.otd.tms.service.application.port.VsoOrderReportPort;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsErrorCode;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.InboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderDetail;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderState;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsInboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsOutboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TransportOrderRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.messaging.TmsEventPayloadSerializer;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.CarrierDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.LogisticsNodeDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportRouteDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 运单应用服务测试（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@DisplayName("运单应用服务 - 受理/调度/事实/回写")
class TransportOrderAppServiceTest {

    private static final String ISO_PATTERN =
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}:\\d{2}";

    private final TransportOrderRepository orderRepository = mock(TransportOrderRepository.class);
    private final TmsOutboxRepository outboxRepository = mock(TmsOutboxRepository.class);
    private final TmsInboxRepository inboxRepository = mock(TmsInboxRepository.class);
    private final VsoOrderReportPort vsoOrderReportPort = mock(VsoOrderReportPort.class);
    private final CarrierDao carrierDao = mock(CarrierDao.class);
    private final TransportRouteDao transportRouteDao = mock(TransportRouteDao.class);
    private final LogisticsNodeDao logisticsNodeDao = mock(LogisticsNodeDao.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TmsProperties properties = new TmsProperties();
    private TransportOrderAppService service;

    @BeforeEach
    void setUp() {
        service = new TransportOrderAppService(orderRepository, outboxRepository, inboxRepository, properties,
                new TmsEventPayloadSerializer(), vsoOrderReportPort, carrierDao, transportRouteDao,
                logisticsNodeDao, ZoneId.of("Asia/Shanghai"));
    }

    private TransportOrder order() {
        return TransportOrder.builder()
                .id(1L)
                .orderNum("TO-1")
                .sourceSystem("VSO")
                .sourceOrderNo("SO-1")
                .state(TransportOrderState.NOT_DISPATCHED)
                .dispatchVersion(0)
                .details(new ArrayList<>(List.of(TransportOrderDetail.builder()
                        .id(10L).orderNum("TO-1").vin("VIN1").state("RESERVED").build())))
                .build();
    }

    private void mockMasterData(boolean enabled) {
        when(carrierDao.selectPoByCode("C1")).thenReturn(CarrierPo.builder().code("C1").enable(enabled).build());
        when(transportRouteDao.selectPoByCode("R1")).thenReturn(
                TransportRoutePo.builder().code("R1").carrierCode("C1").enable(enabled).build());
        when(logisticsNodeDao.selectPoByCode("N1")).thenReturn(
                LogisticsNodePo.builder().code("N1").enable(enabled).warehouseCode("WH-1").build());
        when(logisticsNodeDao.selectPoByCode("N2")).thenReturn(
                LogisticsNodePo.builder().code("N2").enable(enabled).warehouseCode("DDC-1").build());
    }

    @Test
    @DisplayName("受理：新建运单并写 Inbox")
    void shouldAcceptAndCreateOrder() {
        when(inboxRepository.find(anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        when(orderRepository.findBySourceOrder("VSO", "SO-1")).thenReturn(Optional.empty());
        when(orderRepository.countActiveByVin("VIN1", null)).thenReturn(0);

        String orderNum = service.acceptTransportApplied("EVT-1", "vso.transport.applied", "digest",
                new TransportOrderAppService.TransportAppliedMessage("SO-1", "VIN1", "D0001", "R01"));

        assertThat(orderNum).startsWith("TO");
        ArgumentCaptor<TransportOrder> orderCaptor = ArgumentCaptor.forClass(TransportOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getSourceSystem()).isEqualTo("VSO");
        assertThat(orderCaptor.getValue().getSourceOrderNo()).isEqualTo("SO-1");
        assertThat(orderCaptor.getValue().getState()).isEqualTo(TransportOrderState.NOT_DISPATCHED);
        assertThat(orderCaptor.getValue().getDetails()).hasSize(1);
        assertThat(orderCaptor.getValue().getDetails().get(0).getVin()).isEqualTo("VIN1");

        ArgumentCaptor<InboxRecord> inboxCaptor = ArgumentCaptor.forClass(InboxRecord.class);
        verify(inboxRepository).insert(inboxCaptor.capture());
        assertThat(inboxCaptor.getValue().getEventId()).isEqualTo("EVT-1");
        assertThat(inboxCaptor.getValue().getProcessState()).isEqualTo("PROCESSED");
    }

    @Test
    @DisplayName("受理幂等：Inbox 已存在时跳过")
    void shouldSkipWhenInboxExists() {
        when(inboxRepository.find(anyString(), anyString(), eq("EVT-1")))
                .thenReturn(Optional.of(InboxRecord.builder().eventId("EVT-1").build()));

        service.acceptTransportApplied("EVT-1", "vso.transport.applied", "digest",
                new TransportOrderAppService.TransportAppliedMessage("SO-1", "VIN1", null, null));

        verify(orderRepository, never()).save(any());
        verify(inboxRepository, never()).insert(any());
    }

    @Test
    @DisplayName("受理幂等：运单已存在时不重复建单")
    void shouldSkipWhenOrderExists() {
        when(inboxRepository.find(anyString(), anyString(), eq("EVT-2"))).thenReturn(Optional.empty());
        when(orderRepository.findBySourceOrder("VSO", "SO-1")).thenReturn(Optional.of(order()));

        String orderNum = service.acceptTransportApplied("EVT-2", "vso.transport.applied", "digest",
                new TransportOrderAppService.TransportAppliedMessage("SO-1", "VIN1", null, null));

        assertThat(orderNum).isEqualTo("TO-1");
        verify(orderRepository, never()).save(any());
        verify(inboxRepository).insert(any());
    }

    @Test
    @DisplayName("调度：写 WMS 出库指令（schemaVersion=2、ISO 时间、requestId）并回写 VSO 500")
    void shouldDispatchAndEmitOutboundCommand() throws Exception {
        mockMasterData(true);
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order()));

        service.dispatch(new TransportOrderAppService.DispatchCommand("TO-1", "C1", "R1", "N1", "N2",
                "沪A12345", "310101199001010011", new Date(), new Date(), "调度员"));

        ArgumentCaptor<OutboxRecord> captor = ArgumentCaptor.forClass(OutboxRecord.class);
        verify(outboxRepository).insert(captor.capture());
        OutboxRecord record = captor.getValue();
        assertThat(record.getTopic()).isEqualTo("wms.vehicle-outbound.requested");
        assertThat(record.getMessageKey()).isEqualTo("TO-1");
        assertThat(record.getAggregateType()).isEqualTo("TRANSPORT_ORDER");

        JsonNode json = objectMapper.readTree(record.getPayload());
        assertThat(json.get("schemaVersion").asInt()).isEqualTo(2);
        assertThat(json.get("eventType").asText()).isEqualTo("VehicleOutboundRequested");
        assertThat(json.get("occurredAt").asText()).matches(ISO_PATTERN);
        JsonNode payload = json.path("payload");
        assertThat(payload.get("requestId").asText()).isEqualTo("TO-1-1");
        assertThat(payload.get("sourceSystem").asText()).isEqualTo("TMS");
        assertThat(payload.get("sourceOrderNum").asText()).isEqualTo("TO-1");
        assertThat(payload.get("warehouseCode").asText()).isEqualTo("WH-1");
        assertThat(payload.get("vins").get(0).asText()).isEqualTo("VIN1");

        verify(vsoOrderReportPort).prepareTransport("SO-1");
    }

    @Test
    @DisplayName("交车：写 WMS 到货请求（orderNum-IN-seq、交付库仓库、schemaVersion=2）并回写 VSO 600")
    void shouldHandoverAndEmitInboundCommand() throws Exception {
        mockMasterData(true);
        TransportOrder handoverOrder = TransportOrder.builder()
                .id(10L)
                .orderNum("TO-1")
                .sourceSystem("VSO")
                .sourceOrderNo("SO-1")
                .warehouseCode("WH-1")
                .carrierCode("C1")
                .transportRouteCode("R1")
                .dispatchLogisticsNodeCode("N1")
                .handoverLogisticsNodeCode("N2")
                .state(TransportOrderState.IN_TRANSIT)
                .dispatchVersion(1)
                .inboundRequestSeq(0)
                .details(new ArrayList<>(List.of(TransportOrderDetail.builder()
                        .orderNum("TO-1").vin("VIN1").state("OUTBOUND").build())))
                .build();
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(handoverOrder));

        service.handover("TO-1", List.of("VIN1"), true, "运营员");

        ArgumentCaptor<OutboxRecord> captor = ArgumentCaptor.forClass(OutboxRecord.class);
        verify(outboxRepository).insert(captor.capture());
        OutboxRecord record = captor.getValue();
        assertThat(record.getTopic()).isEqualTo("wms.vehicle-inbound.requested");
        assertThat(record.getMessageKey()).isEqualTo("TO-1");
        assertThat(record.getAggregateVersion()).isEqualTo(1L);

        JsonNode json = objectMapper.readTree(record.getPayload());
        assertThat(json.get("eventType").asText()).isEqualTo("VehicleInboundRequested");
        assertThat(json.get("schemaVersion").asInt()).isEqualTo(2);
        assertThat(json.get("occurredAt").asText()).matches(ISO_PATTERN);
        JsonNode payload = json.path("payload");
        assertThat(payload.get("requestId").asText()).isEqualTo("TO-1-IN-1");
        assertThat(payload.get("sourceOrderNum").asText()).isEqualTo("TO-1");
        assertThat(payload.get("warehouseCode").asText()).isEqualTo("DDC-1");
        assertThat(payload.get("vins").get(0).asText()).isEqualTo("VIN1");

        verify(vsoOrderReportPort).prepareDelivery("SO-1");
    }

    @Test
    @DisplayName("调度：主数据停用时拒绝")
    void shouldRejectDispatchWhenMasterDataDisabled() {
        mockMasterData(false);
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order()));

        assertThatThrownBy(() -> service.dispatch(new TransportOrderAppService.DispatchCommand("TO-1", "C1", "R1",
                "N1", "N2", null, null, null, null, "调度员")))
                .isInstanceOf(TmsBusinessException.class)
                .hasMessageContaining("承运商");
        verify(outboxRepository, never()).insert(any());
    }

    @Test
    @DisplayName("出库事实：更新明细为 OUTBOUND 并写 Inbox")
    void shouldApplyOutboundFact() {
        when(inboxRepository.find(anyString(), anyString(), eq("EVT-3"))).thenReturn(Optional.empty());
        TransportOrder order = order();
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order));

        service.applyOutboundFact("EVT-3", "wms.vehicle-outbound.changed", "digest", "TO-1", "OB-1",
                List.of(new TransportOrderAppService.WmsOutboundItem("VIN1", "OUTBOUND")));

        assertThat(order.getDetails().get(0).getState()).isEqualTo("OUTBOUND");
        assertThat(order.getDetails().get(0).getOutboundOrderNum()).isEqualTo("OB-1");
        verify(orderRepository).save(order);
        verify(inboxRepository).insert(any());
    }

    @Test
    @DisplayName("出库拒绝：记录原因码并标记业务拒绝")
    void shouldMarkRejection() {
        when(inboxRepository.find(anyString(), anyString(), eq("EVT-4"))).thenReturn(Optional.empty());
        TransportOrder order = order();
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order));

        service.applyOutboundRejection("EVT-4", "wms.vehicle-outbound.command-rejected", "digest", "TO-1", "302003");

        assertThat(order.getRejectReason()).isEqualTo("302003");
        ArgumentCaptor<InboxRecord> captor = ArgumentCaptor.forClass(InboxRecord.class);
        verify(inboxRepository).insert(captor.capture());
        assertThat(captor.getValue().getProcessState()).isEqualTo("BUSINESS_REJECTED");
        assertThat(captor.getValue().getErrorCode()).isEqualTo("302003");
    }

    @Test
    @DisplayName("取消：发出库取消指令，不回写订单状态")
    void shouldEmitCancellationOnCancel() throws Exception {
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order()));

        service.cancel("TO-1", "客户退单", "运营员");

        ArgumentCaptor<OutboxRecord> captor = ArgumentCaptor.forClass(OutboxRecord.class);
        verify(outboxRepository).insert(captor.capture());
        assertThat(captor.getValue().getTopic()).isEqualTo("wms.vehicle-outbound.cancellation-requested");
        assertThat(captor.getValue().getPayload()).contains("VehicleOutboundCancellationRequested");
        verify(vsoOrderReportPort, never()).prepareTransport(anyString());
        verify(vsoOrderReportPort, never()).transporting(anyString());
        verify(vsoOrderReportPort, never()).prepareDelivery(anyString());
    }

    @Test
    @DisplayName("补偿：VSO 待发运订单缺失运单时补建")
    void shouldCompensateMissingOrder() {
        when(orderRepository.findBySourceOrder("VSO", "SO-9")).thenReturn(Optional.empty());
        when(orderRepository.countActiveByVin(anyString(), any())).thenReturn(0);

        boolean created = service.compensate(new VsoOrderReportPort.VsoTransportAppliedOrder(
                "SO-9", "VIN9", "D0001", "R01"));

        assertThat(created).isTrue();
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("发车：未全部出库时拒绝并给出 203002")
    void shouldRejectDepartWhenNotOutbound() {
        when(orderRepository.findByOrderNum("TO-1")).thenReturn(Optional.of(order()));

        assertThatThrownBy(() -> service.depart("TO-1", "运营员"))
                .isInstanceOf(TmsBusinessException.class)
                .hasMessageContaining("未出库");
        assertThat(TmsErrorCode.STATE_NOT_ALLOWED.getCode()).isEqualTo("203002");
    }
}
