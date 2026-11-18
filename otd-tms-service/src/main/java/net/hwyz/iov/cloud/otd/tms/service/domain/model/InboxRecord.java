package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * 入站消息处理记录（领域模型）
 *
 * @author hwyz_leo
 */
@Getter
@Builder
public class InboxRecord {

    private Long id;
    private String consumerName;
    private String topic;
    private String eventId;
    private String eventType;
    private String payloadDigest;
    private String processState;
    private String businessKey;
    private String resultEventId;
    private String errorCode;
    private String errorMessage;
    private Date receivedTime;
    private Date processedTime;
}
