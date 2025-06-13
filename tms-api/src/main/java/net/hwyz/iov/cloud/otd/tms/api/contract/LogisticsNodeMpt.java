package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台物流据点信息
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LogisticsNodeMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 据点代码
     */
    private String code;

    /**
     * 据点名称
     */
    private String name;

    /**
     * 据点类型代码
     */
    private String typeCode;

    /**
     * 省级行政区代码
     */
    private String provinceCode;

    /**
     * 地区级行政区代码
     */
    private String cityCode;

    /**
     * 县级行政区代码
     */
    private String countyCode;

    /**
     * 据点地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 是否启用
     */
    private Byte enable;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

}
