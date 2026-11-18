package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.hwyz.iov.cloud.framework.mysql.po.BasePo;

import java.util.Date;

/**
 * <p>
 * TMS 出站消息发件箱 数据对象
 * </p>
 *
 * @author hwyz_leo
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("tb_tms_outbox")
public class TmsOutboxPo extends BasePo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("event_type")
    private String eventType;

    @TableField("aggregate_type")
    private String aggregateType;

    @TableField("aggregate_id")
    private String aggregateId;

    @TableField("aggregate_version")
    private Long aggregateVersion;

    @TableField("topic")
    private String topic;

    @TableField("message_key")
    private String messageKey;

    @TableField("payload")
    private String payload;

    @TableField("publish_state")
    private String publishState;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("next_retry_time")
    private Date nextRetryTime;

    @TableField("published_at")
    private Date publishedAt;

    @TableField("last_error")
    private String lastError;
}
