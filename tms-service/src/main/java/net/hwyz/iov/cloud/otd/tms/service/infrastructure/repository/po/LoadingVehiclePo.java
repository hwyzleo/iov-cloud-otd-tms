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
 * 装载车 数据对象
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
@TableName("tb_loading_vehicle")
public class LoadingVehiclePo extends BasePo {

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
     * 车牌
     */
    @TableField("license_plate")
    private String licensePlate;

    /**
     * 车型：C6-6位车，C7-7位车，C8-8位车，RV-救援车
     */
    @TableField("vehicle_type")
    private String vehicleType;

    /**
     * 最大装载数
     */
    @TableField("max_loading_capacity")
    private Short maxLoadingCapacity;

    /**
     * 行驶证号
     */
    @TableField("vehicle_registration_number")
    private String vehicleRegistrationNumber;

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
