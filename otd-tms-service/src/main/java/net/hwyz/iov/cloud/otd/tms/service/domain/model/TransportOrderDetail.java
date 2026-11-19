package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * 运输单 VIN 明细（领域模型）
 *
 * @author hwyz_leo
 */
@Getter
@Builder
public class TransportOrderDetail {

    /**
     * 主键
     */
    private Long id;

    /**
     * 运输单号
     */
    private String orderNum;

    /**
     * 车架号
     */
    private String vin;

    /**
     * WMS 明细状态快照：RESERVED / LOADED / OUTBOUND / RELEASED
     */
    private String state;

    /**
     * WMS 出库单号
     */
    private String outboundOrderNum;

    /**
     * 装载道
     */
    private Integer loadingLine;
    /**
     * 交车时间（单 VIN）
     */
    private Date deliveryTime;

    /**
     * 是否锁定
     */
    private Boolean lock;

    /**
     * 更新 WMS 事实
     *
     * @param state            明细状态
     * @param outboundOrderNum WMS 出库单号
     */
    public void applyOutboundFact(String state, String outboundOrderNum) {
        this.state = state;
        if (outboundOrderNum != null && !outboundOrderNum.isBlank()) {
            this.outboundOrderNum = outboundOrderNum;
        }
    }

    /**
     * 是否已出库
     *
     * @return 已出库返回 true
     */
    public boolean isOutbound() {
        return "OUTBOUND".equals(state);
    }

    /**
     * 是否已装车或出库（取消拦截条件）
     *
     * @return 已装车或出库返回 true
     */
    public boolean isLoadedOrOutbound() {
        return "LOADED".equals(state) || "OUTBOUND".equals(state);
    }

    /**
     * 标记该 VIN 已交车
     *
     * @param now 交车时间
     */
    public void markDelivered(Date now) {
        this.deliveryTime = now;
    }

    /**
     * 是否已交车
     *
     * @return 已交车返回 true
     */
    public boolean isDelivered() {
        return deliveryTime != null;
    }
}
