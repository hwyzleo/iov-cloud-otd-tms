package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.*;
import net.hwyz.iov.cloud.framework.common.web.domain.BaseRequest;

import java.util.Date;

/**
 * 管理后台运输方式
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransportTypeMpt extends BaseRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 运输方式代码
     */
    private String code;

    /**
     * 运输方式名称
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
