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
 * 物流据点 数据对象
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-13
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_logistics_node")
public class LogisticsNodePo extends BasePo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 据点代码
     */
    @TableField("code")
    private String code;

    /**
     * 据点名称
     */
    @TableField("name")
    private String name;

    /**
     * 据点类型代码
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * 省级行政区代码
     */
    @TableField("province_code")
    private String provinceCode;

    /**
     * 地区级行政区代码
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 县级行政区代码
     */
    @TableField("county_code")
    private String countyCode;

    /**
     * 据点地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField("contact_number")
    private String contactNumber;

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
