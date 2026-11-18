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
 * TMS 入站消息处理记录 数据对象
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
@TableName("tb_tms_inbox")
public class TmsInboxPo extends BasePo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("consumer_name")
    private String consumerName;

    @TableField("topic")
    private String topic;

    @TableField("event_id")
    private String eventId;

    @TableField("event_type")
    private String eventType;

    @TableField("payload_digest")
    private String payloadDigest;

    @TableField("process_state")
    private String processState;

    @TableField("business_key")
    private String businessKey;

    @TableField("result_event_id")
    private String resultEventId;

    @TableField("error_code")
    private String errorCode;

    @TableField("error_message")
    private String errorMessage;

    @TableField("received_time")
    private Date receivedTime;

    @TableField("processed_time")
    private Date processedTime;
}
