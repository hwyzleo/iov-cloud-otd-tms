package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台物流据点类型
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LogisticsNodeTypeMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 据点类型代码
     */
    private String code;

    /**
     * 据点类型名称
     */
    private String name;

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
