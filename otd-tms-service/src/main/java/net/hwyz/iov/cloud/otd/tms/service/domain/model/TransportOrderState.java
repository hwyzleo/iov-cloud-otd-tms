package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * 运输单状态（OTD-TMS-DSN-CR-001 §1.4）
 * <p>
 * 编码沿用既有 tb_transport_order.state：1-未发运，2-运输中，3-部分交车，4-交车完成，
 * 5-部分接车，6-接车完成，7-手工结束，9-已取消。
 *
 * @author hwyz_leo
 */
@Getter
@AllArgsConstructor
public enum TransportOrderState {

    NOT_DISPATCHED(1, "未发运"),
    IN_TRANSIT(2, "运输中"),
    PARTIALLY_HANDED_OVER(3, "部分交车"),
    HANDED_OVER(4, "交车完成"),
    PARTIALLY_RECEIVED(5, "部分接车"),
    RECEIVED(6, "接车完成"),
    MANUALLY_CLOSED(7, "手工结束"),
    CANCELLED(9, "已取消");

    private final int code;
    private final String description;

    private static final Set<TransportOrderState> TERMINAL =
            EnumSet.of(RECEIVED, MANUALLY_CLOSED, CANCELLED);

    /**
     * 按编码解析
     *
     * @param code 编码
     * @return 状态；未知返回 null
     */
    public static TransportOrderState fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(state -> state.code == code)
                .findFirst()
                .orElse(null);
    }

    /**
     * 是否终止态
     *
     * @return 终止态返回 true
     */
    public boolean isTerminal() {
        return TERMINAL.contains(this);
    }

    /**
     * 是否允许流转到目标状态
     *
     * @param target 目标状态
     * @return 允许返回 true
     */
    public boolean canTransitionTo(TransportOrderState target) {
        if (target == null || isTerminal()) {
            return false;
        }
        return switch (this) {
            case NOT_DISPATCHED -> target == IN_TRANSIT || target == CANCELLED;
            case IN_TRANSIT -> target == PARTIALLY_HANDED_OVER || target == HANDED_OVER
                    || target == MANUALLY_CLOSED;
            case PARTIALLY_HANDED_OVER -> target == HANDED_OVER || target == MANUALLY_CLOSED;
            case HANDED_OVER -> target == PARTIALLY_RECEIVED || target == RECEIVED;
            case PARTIALLY_RECEIVED -> target == RECEIVED;
            default -> false;
        };
    }
}
