package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台运输线路
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransportRouteMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 承运商代码
     */
    private String carrierCode;

    /**
     * 线路代码
     */
    private String code;

    /**
     * 线路名称
     */
    private String name;

    /**
     * 线路规划
     */
    private String routePlan;

    /**
     * 线路时长
     */
    private Integer routeHours;

    /**
     * 线路距离
     */
    private Integer routeKm;

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
