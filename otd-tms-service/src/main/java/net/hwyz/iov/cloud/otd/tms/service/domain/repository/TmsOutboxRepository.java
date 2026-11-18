package net.hwyz.iov.cloud.otd.tms.service.domain.repository;

import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;

import java.util.Date;
import java.util.List;

/**
 * 出站消息仓储（Outbox，OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
public interface TmsOutboxRepository {

    /**
     * 写入待发布记录（与业务同一本地事务）
     *
     * @param record 记录
     */
    void insert(OutboxRecord record);

    /**
     * 扫描可发布记录
     *
     * @param now   当前时间
     * @param limit 批大小
     * @return 记录列表
     */
    List<OutboxRecord> listPublishable(Date now, int limit);

    /**
     * 标记已发布
     *
     * @param id 主键
     * @return 影响行数
     */
    int markPublished(Long id);

    /**
     * 标记可重试
     *
     * @param id            主键
     * @param retryCount    已重试次数
     * @param nextRetryTime 下次重试时间
     * @param error         脱敏错误
     * @return 影响行数
     */
    int markRetryable(Long id, int retryCount, Date nextRetryTime, String error);

    /**
     * 标记终态失败
     *
     * @param id    主键
     * @param error 脱敏错误
     * @return 影响行数
     */
    int markDead(Long id, String error);
}
