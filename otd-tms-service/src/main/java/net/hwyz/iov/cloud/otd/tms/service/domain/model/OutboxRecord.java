package net.hwyz.iov.cloud.otd.tms.service.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * 出站消息记录（领域模型）
 *
 * @author hwyz_leo
 */
@Getter
@Builder
public class OutboxRecord {

    private Long id;
    private String eventId;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private Long aggregateVersion;
    private String topic;
    private String messageKey;
    private String payload;
    private String publishState;
    private Integer retryCount;
    private Date nextRetryTime;
    private Date publishedAt;
    private String lastError;
}
