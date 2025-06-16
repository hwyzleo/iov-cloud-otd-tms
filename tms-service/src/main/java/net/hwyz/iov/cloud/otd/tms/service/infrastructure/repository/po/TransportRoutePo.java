package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import net.hwyz.iov.cloud.framework.mysql.po.BasePo;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 运输线路 数据对象
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_transport_route")
public class TransportRoutePo extends BasePo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 承运商代码
     */
    @TableField("carrier_code")
    private String carrierCode;

    /**
     * 线路代码
     */
    @TableField("code")
    private String code;

    /**
     * 线路名称
     */
    @TableField("name")
    private String name;

    /**
     * 线路规划
     */
    @TableField("route_plan")
    private String routePlan;

    /**
     * 线路时长
     */
    @TableField("route_hours")
    private Integer routeHours;

    /**
     * 线路距离
     */
    @TableField("route_km")
    private Integer routeKm;

    /**
     * 是否启用
     */
    @TableField("enable")
    private Boolean enable;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;
}
