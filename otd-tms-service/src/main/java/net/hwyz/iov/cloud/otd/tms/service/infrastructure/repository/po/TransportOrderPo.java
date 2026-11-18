package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.hwyz.iov.cloud.framework.mysql.po.BasePo;

import java.util.Date;

/**
 * <p>
 * 运输订单 数据对象
 * </p>
 *
 * @author hwyz_leo
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("tb_transport_order")
public class TransportOrderPo extends BasePo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_num")
    private String orderNum;

    @TableField("source_system")
    private String sourceSystem;

    @TableField("source_order_no")
    private String sourceOrderNo;

    @TableField("warehouse_code")
    private String warehouseCode;

    @TableField("carrier_code")
    private String carrierCode;

    @TableField("state")
    private Integer state;

    @TableField("transport_route_code")
    private String transportRouteCode;

    @TableField("dispatch_logistics_node_code")
    private String dispatchLogisticsNodeCode;

    @TableField("estimated_loading_time")
    private Date estimatedLoadingTime;

    @TableField("estimated_arrival_time")
    private Date estimatedArrivalTime;

    @TableField("vehicle_dispatch_time")
    private Date vehicleDispatchTime;

    @TableField("handover_logistics_node_code")
    private String handoverLogisticsNodeCode;

    @TableField("delivery_time")
    private Date deliveryTime;

    @TableField("receipt_time")
    private Date receiptTime;

    @TableField("license_plate")
    private String licensePlate;

    @TableField("id_number")
    private String idNumber;

    @TableField("dispatcher")
    private String dispatcher;

    @TableField("dispatch_time")
    private Date dispatchTime;

    @TableField("confirm")
    private Boolean confirm;

    @TableField("dispatch_version")
    private Integer dispatchVersion;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("last_reported_vso_state")
    private Integer lastReportedVsoState;
}
