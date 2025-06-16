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
 * 运输方式 数据对象
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
@TableName("tb_transport_type")
public class TransportTypePo extends BasePo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运输方式代码
     */
    @TableField("code")
    private String code;

    /**
     * 运输方式名称
     */
    @TableField("name")
    private String name;

    /**
     * 是否启用
     */
    @TableField("enable")
    private Byte enable;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;
}
