package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import lombok.Builder;
import lombok.Getter;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsErrorCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 运输单聚合根（OTD-TMS-DSN-CR-001）
 * <p>
 * 不变量：状态机单向、VIN 在有效运单内唯一、未装车前才可取消、出库指令按调度版本幂等；
 * 订单状态以 VSO 为唯一事实，库存与出库事实以 WMS 为唯一事实，本聚合只持有承运过程事实。
 *
 * @author hwyz_leo
 */
@Getter
@Builder
public class TransportOrder {

    /**
     * 主键
     */
    private Long id;

    /**
     * 运输单号
     */
    private String orderNum;

    /**
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 来源订单号（VSO 订单号）
     */
    private String sourceOrderNo;

    /**
     * 出库仓库代码（可空，空则由 WMS 默认仓库兜底）
     */
    private String warehouseCode;

    /**
     * 承运商代码
     */
    private String carrierCode;

    /**
     * 运输线路代码
     */
    private String transportRouteCode;

    /**
     * 发车据点代码
     */
    private String dispatchLogisticsNodeCode;

    /**
     * 接车据点代码
     */
    private String handoverLogisticsNodeCode;

    /**
     * 状态
     */
    private TransportOrderState state;

    /**
     * 预计装车时间
     */
    private Date estimatedLoadingTime;

    /**
     * 预计到达时间
     */
    private Date estimatedArrivalTime;

    /**
     * 发车时间
     */
    private Date vehicleDispatchTime;

    /**
     * 交车时间
     */
    private Date deliveryTime;

    /**
     * 接车时间
     */
    private Date receiptTime;

    /**
     * 运输车辆
     */
    private String licensePlate;

    /**
     * 驾驶员身份证号
     */
    private String idNumber;

    /**
     * 调度人员
     */
    private String dispatcher;

    /**
     * 调度时间
     */
    private Date dispatchTime;

    /**
     * 是否确认
     */
    private Boolean confirm;

    /**
     * 调度版本（出库指令幂等依据）
     */
    private Integer dispatchVersion;

    /**
     * 出库拒绝原因码
     */
    private String rejectReason;

    /**
     * 已回写 VSO 的订单状态值
     */
    private Integer lastReportedVsoState;

    /**
     * VIN 明细
     */
    @Builder.Default
    private List<TransportOrderDetail> details = new ArrayList<>();

    /**
     * 调度并生成出库请求标识（requestId = 运单号-调度版本）
     *
     * @param carrierCode               承运商
     * @param transportRouteCode        线路
     * @param dispatchLogisticsNodeCode 发车据点
     * @param handoverLogisticsNodeCode 接车据点
     * @param licensePlate              运输车辆
     * @param idNumber                  驾驶员身份证号
     * @param estimatedLoadingTime      预计装车时间
     * @param estimatedArrivalTime      预计到达时间
     * @param dispatcher                调度人员
     * @param now                       当前时间
     * @return 出库请求标识
     */
    public String dispatch(String carrierCode, String transportRouteCode, String dispatchLogisticsNodeCode,
                           String handoverLogisticsNodeCode, String licensePlate, String idNumber,
                           Date estimatedLoadingTime, Date estimatedArrivalTime, String dispatcher, Date now) {
        requireState(TransportOrderState.NOT_DISPATCHED, "调度");
        requireNotBlank(carrierCode, "承运商不能为空");
        requireNotBlank(transportRouteCode, "运输线路不能为空");
        requireNotBlank(dispatchLogisticsNodeCode, "发车据点不能为空");
        requireNotBlank(handoverLogisticsNodeCode, "接车据点不能为空");
        this.carrierCode = carrierCode;
        this.transportRouteCode = transportRouteCode;
        this.dispatchLogisticsNodeCode = dispatchLogisticsNodeCode;
        this.handoverLogisticsNodeCode = handoverLogisticsNodeCode;
        this.licensePlate = licensePlate;
        this.idNumber = idNumber;
        this.estimatedLoadingTime = estimatedLoadingTime;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.dispatcher = dispatcher;
        this.dispatchTime = now;
        this.confirm = Boolean.FALSE;
        this.dispatchVersion = (this.dispatchVersion == null ? 0 : this.dispatchVersion) + 1;
        return requestId();
    }

    /**
     * 出库请求标识
     *
     * @return requestId
     */
    public String requestId() {
        return orderNum + "-" + (dispatchVersion == null ? 0 : dispatchVersion);
    }

    /**
     * 应用 WMS 出库事实
     *
     * @param vin             车架号
     * @param detailState     明细状态
     * @param outboundOrderNo WMS 出库单号
     */
    public void applyOutboundFact(String vin, String detailState, String outboundOrderNo) {
        TransportOrderDetail detail = details.stream()
                .filter(item -> item.getVin().equals(vin))
                .findFirst()
                .orElseThrow(() -> new TmsBusinessException(TmsErrorCode.VIN_NOT_IN_ORDER,
                        "VIN[" + vin + "]不属于运单[" + orderNum + "]"));
        detail.applyOutboundFact(detailState, outboundOrderNo);
    }

    /**
     * 标记 WMS 拒绝事实
     *
     * @param reasonCode 原因码
     */
    public void markRejected(String reasonCode) {
        this.rejectReason = reasonCode;
    }

    /**
     * 确认发车（必须全部 VIN 已出库）
     *
     * @param now 当前时间
     */
    public void depart(Date now) {
        requireState(TransportOrderState.NOT_DISPATCHED, "发车");
        if (details.isEmpty() || !details.stream().allMatch(TransportOrderDetail::isOutbound)) {
            throw new TmsBusinessException(TmsErrorCode.STATE_NOT_ALLOWED, "运单[" + orderNum + "]存在未出库 VIN，不允许发车");
        }
        transitTo(TransportOrderState.IN_TRANSIT);
        this.vehicleDispatchTime = now;
        this.confirm = Boolean.TRUE;
    }

    /**
     * 交车（承运商交付接车据点）
     *
     * @param allDelivered 是否全部明细交车完成
     * @param now          当前时间
     */
    public void handover(boolean allDelivered, Date now) {
        transitTo(allDelivered ? TransportOrderState.HANDED_OVER : TransportOrderState.PARTIALLY_HANDED_OVER);
        if (this.deliveryTime == null) {
            this.deliveryTime = now;
        }
    }

    /**
     * 接车（接车方确认接收）
     *
     * @param allReceived 是否全部明细接车完成
     * @param now         当前时间
     */
    public void receipt(boolean allReceived, Date now) {
        transitTo(allReceived ? TransportOrderState.RECEIVED : TransportOrderState.PARTIALLY_RECEIVED);
        if (this.receiptTime == null) {
            this.receiptTime = now;
        }
    }

    /**
     * 取消运单（仅未装车且未发车）
     *
     * @param reason 取消原因
     */
    public void cancel(String reason) {
        requireState(TransportOrderState.NOT_DISPATCHED, "取消");
        if (details.stream().anyMatch(TransportOrderDetail::isLoadedOrOutbound)) {
            throw new TmsBusinessException(TmsErrorCode.STATE_NOT_ALLOWED, "运单[" + orderNum + "]存在已装车或已出库 VIN，不允许取消");
        }
        transitTo(TransportOrderState.CANCELLED);
        this.rejectReason = reason;
    }

    /**
     * 手工结束
     */
    public void manualClose() {
        transitTo(TransportOrderState.MANUALLY_CLOSED);
    }

    /**
     * 记录已回写 VSO 的订单状态
     *
     * @param vsoState VSO 订单状态值
     */
    public void markVsoReported(Integer vsoState) {
        this.lastReportedVsoState = vsoState;
    }

    /**
     * 是否已完成（接车完成）
     *
     * @return 已完成返回 true
     */
    public boolean isReceived() {
        return state == TransportOrderState.RECEIVED;
    }

    private void transitTo(TransportOrderState target) {
        if (state == null || !state.canTransitionTo(target)) {
            throw new TmsBusinessException(TmsErrorCode.STATE_NOT_ALLOWED,
                    "运单[" + orderNum + "]当前状态[" + (state == null ? "null" : state.getDescription())
                            + "]不允许流转为[" + target.getDescription() + "]");
        }
        this.state = target;
    }

    private void requireState(TransportOrderState expected, String action) {
        if (this.state != expected) {
            throw new TmsBusinessException(TmsErrorCode.STATE_NOT_ALLOWED,
                    "运单[" + orderNum + "]当前状态[" + (state == null ? "null" : state.getDescription())
                            + "]不允许" + action);
        }
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new TmsBusinessException(TmsErrorCode.MASTER_DATA_UNAVAILABLE, message);
        }
    }
}
