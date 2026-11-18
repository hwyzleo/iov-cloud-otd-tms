package net.hwyz.iov.cloud.otd.tms.api.contract;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import net.hwyz.iov.cloud.framework.common.bean.BaseRequest;

import java.util.Date;

/**
 * 管理后台运输单动作请求（OTD-TMS-DSN-CR-001）
 * <p>
 * 调度、发车、交车、接车、取消、手工结束共用同一请求模型，按动作取用相关字段。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransportOrderActionMpt extends BaseRequest {

    /**
     * 运输单号
     */
    @NotBlank(message = "运输单号不能为空")
    private String orderNum;

    /**
     * 承运商代码（调度必填）
     */
    private String carrierCode;

    /**
     * 运输线路代码（调度必填）
     */
    private String transportRouteCode;

    /**
     * 发车据点代码（调度必填）
     */
    private String dispatchLogisticsNodeCode;

    /**
     * 接车据点代码（调度必填）
     */
    private String handoverLogisticsNodeCode;

    /**
     * 运输车辆
     */
    private String licensePlate;

    /**
     * 驾驶员身份证号
     */
    private String idNumber;

    /**
     * 预计装车时间
     */
    private Date estimatedLoadingTime;

    /**
     * 预计到达时间
     */
    private Date estimatedArrivalTime;

    /**
     * 调度人员
     */
    private String dispatcher;

    /**
     * 是否全部交车/接车完成（交车、接车动作使用）
     */
    private Boolean allCompleted;

    /**
     * 取消原因
     */
    private String reason;
}
