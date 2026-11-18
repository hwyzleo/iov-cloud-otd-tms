package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import org.junit.jupiter.api.DisplayName;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * 运输单聚合测试（OTD-TMS-DSN-CR-001 INV-005 / INV-004 / INV-009）
 *
 * @author hwyz_leo
 */
@DisplayName("运输单聚合 - 状态机与不变量")
class TransportOrderTest {

    private TransportOrder order(String vin) {
        return TransportOrder.builder()
                .orderNum("TO-1")
                .sourceSystem("VSO")
                .sourceOrderNo("SO-1")
                .state(TransportOrderState.NOT_DISPATCHED)
                .dispatchVersion(0)
                .details(new java.util.ArrayList<>(java.util.List.of(
                        TransportOrderDetail.builder().orderNum("TO-1").vin(vin).state("RESERVED").build())))
                .build();
    }

    @Test
    @DisplayName("调度生成幂等 requestId，并保留未发运状态")
    void shouldDispatchAndProduceRequestId() {
        TransportOrder order = order("VIN1");

        String requestId = order.dispatch("C1", "R1", "N1", "N2", "沪A12345", "310101199001010011",
                new Date(), new Date(), "调度员", new Date());

        assertThat(requestId).isEqualTo("TO-1-1");
        assertThat(order.getState()).isEqualTo(TransportOrderState.NOT_DISPATCHED);
        assertThat(order.getDispatchVersion()).isEqualTo(1);
        assertThat(order.getCarrierCode()).isEqualTo("C1");
        assertThat(order.getDispatchTime()).isNotNull();
    }

    @Test
    @DisplayName("未出库不允许发车")
    void shouldRejectDepartBeforeAllOutbound() {
        TransportOrder order = order("VIN1");
        order.dispatch("C1", "R1", "N1", "N2", null, null, null, null, "调度员", new Date());

        TmsBusinessException error = catchThrowableOfType(() -> order.depart(new Date()),
                TmsBusinessException.class);
        assertThat(error.getMessage()).contains("未出库");
        assertThat(error.getCode()).isEqualTo("203002");
    }

    @Test
    @DisplayName("全部出库后可发车并单向流转至接车完成")
    void shouldFlowFromDepartToReceived() {
        TransportOrder order = order("VIN1");
        order.dispatch("C1", "R1", "N1", "N2", null, null, null, null, "调度员", new Date());
        order.applyOutboundFact("VIN1", "OUTBOUND", "OB-1");

        order.depart(new Date());
        assertThat(order.getState()).isEqualTo(TransportOrderState.IN_TRANSIT);

        order.handover(false, new Date());
        assertThat(order.getState()).isEqualTo(TransportOrderState.PARTIALLY_HANDED_OVER);
        order.handover(true, new Date());
        assertThat(order.getState()).isEqualTo(TransportOrderState.HANDED_OVER);

        order.receipt(false, new Date());
        assertThat(order.getState()).isEqualTo(TransportOrderState.PARTIALLY_RECEIVED);
        order.receipt(true, new Date());
        assertThat(order.getState()).isEqualTo(TransportOrderState.RECEIVED);
        assertThat(order.isReceived()).isTrue();
    }

    @Test
    @DisplayName("已装车或已出库不允许取消")
    void shouldRejectCancelAfterLoading() {
        TransportOrder order = order("VIN1");
        order.applyOutboundFact("VIN1", "LOADED", "OB-1");

        assertThatThrownBy(() -> order.cancel("客户退单"))
                .hasMessageContaining("不允许取消");
        assertThat(order.getState()).isEqualTo(TransportOrderState.NOT_DISPATCHED);
    }

    @Test
    @DisplayName("未装车可取消，取消后为终止态")
    void shouldCancelBeforeLoading() {
        TransportOrder order = order("VIN1");

        order.cancel("客户退单");

        assertThat(order.getState()).isEqualTo(TransportOrderState.CANCELLED);
        assertThat(order.getState().isTerminal()).isTrue();
        assertThatThrownBy(() -> order.depart(new Date())).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("WMS 事实更新未知名 VIN 被拒绝")
    void shouldRejectUnknownVinFact() {
        TransportOrder order = order("VIN1");

        TmsBusinessException error = catchThrowableOfType(
                () -> order.applyOutboundFact("VIN-OTHER", "OUTBOUND", "OB-1"), TmsBusinessException.class);
        assertThat(error.getMessage()).contains("不属于运单");
        assertThat(error.getCode()).isEqualTo("203009");
    }

    @Test
    @DisplayName("状态编码映射与终止态判定")
    void shouldMapStateCodes() {
        assertThat(TransportOrderState.fromCode(1)).isEqualTo(TransportOrderState.NOT_DISPATCHED);
        assertThat(TransportOrderState.fromCode(9)).isEqualTo(TransportOrderState.CANCELLED);
        assertThat(TransportOrderState.fromCode(99)).isNull();
        assertThat(TransportOrderState.MANUALLY_CLOSED.isTerminal()).isTrue();
        assertThat(TransportOrderState.NOT_DISPATCHED.canTransitionTo(TransportOrderState.IN_TRANSIT)).isTrue();
        assertThat(TransportOrderState.NOT_DISPATCHED.canTransitionTo(TransportOrderState.HANDED_OVER)).isFalse();
    }
}
