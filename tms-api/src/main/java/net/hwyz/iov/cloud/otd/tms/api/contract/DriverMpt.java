package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台驾驶员
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 承运商代码
     */
    private String carrierCode;

    /**
     * 驾驶员姓名
     */
    private String realName;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 驾驶证号
     */
    private String drivingLicenseNumber;

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
