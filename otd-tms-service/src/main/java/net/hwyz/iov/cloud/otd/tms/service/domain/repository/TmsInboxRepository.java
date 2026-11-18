package net.hwyz.iov.cloud.otd.tms.service.domain.repository;

import net.hwyz.iov.cloud.otd.tms.service.domain.model.InboxRecord;

import java.util.Optional;

/**
 * 入站消息去重仓储（Inbox，OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
public interface TmsInboxRepository {

    /**
     * 查询已记录的处理结果
     *
     * @param consumerName 消费者名称
     * @param topic        Topic
     * @param eventId      事件 ID
     * @return Inbox 记录
     */
    Optional<InboxRecord> find(String consumerName, String topic, String eventId);

    /**
     * 写入处理结果（与业务同一本地事务）
     *
     * @param record 记录
     */
    void insert(InboxRecord record);
}
