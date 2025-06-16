package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台装载车
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoadingVehicleMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 承运商代码
     */
    private String carrierCode;

    /**
     * 车牌
     */
    private String licensePlate;

    /**
     * 车型：C6-6位车，C7-7位车，C8-8位车，RV-救援车
     */
    private String vehicleType;

    /**
     * 最大装载数
     */
    private Short maxLoadingCapacity;

    /**
     * 行驶证号
     */
    private String vehicleRegistrationNumber;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

}
