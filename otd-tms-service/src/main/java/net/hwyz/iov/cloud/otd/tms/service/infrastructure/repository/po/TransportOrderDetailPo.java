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
 * 运输订单明细 数据对象
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
@TableName("tb_transport_order_detail")
public class TransportOrderDetailPo extends BasePo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_num")
    private String orderNum;

    @TableField("vin")
    private String vin;

    @TableField("state")
    private String state;

    @TableField("outbound_order_num")
    private String outboundOrderNum;

    @TableField("loading_line")
    private Integer loadingLine;

    @TableField("delivery_time")
    private Date deliveryTime;

    @TableField("receipt_time")
    private Date receiptTime;

    @TableField("lock")
    private Boolean lock;
}
