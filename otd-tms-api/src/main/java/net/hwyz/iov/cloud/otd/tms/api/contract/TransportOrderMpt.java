package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.bean.BaseRequest;

import java.util.Date;
import java.util.List;

/**
 * 管理后台运输单信息（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransportOrderMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 运输单号
     */
    private String orderNum;

    /**
     * 来源订单号（VSO 订单号）
     */
    private String sourceOrderNo;

    /**
     * 状态：1-未发运，2-运输中，3-部分交车，4-交车完成，5-部分接车，6-接车完成，7-手工结束，9-已取消
     */
    private Integer state;

    /**
     * 状态描述
     */
    private String stateName;

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
     * 出库仓库代码
     */
    private String warehouseCode;

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
     * 调度人员
     */
    private String dispatcher;

    /**
     * 调度时间
     */
    private Date dispatchTime;

    /**
     * 出库拒绝原因码
     */
    private String rejectReason;

    /**
     * 已回写 VSO 的订单状态值
     */
    private Integer lastReportedVsoState;

    /**
     * VIN 列表
     */
    private List<String> vins;
}
