package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsOutboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TmsOutboxDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TmsOutboxPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出站消息仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class TmsOutboxRepositoryImpl implements TmsOutboxRepository {

    private final TmsOutboxDao tmsOutboxDao;

    @Override
    public void insert(OutboxRecord record) {
        tmsOutboxDao.insertPo(TmsOutboxPo.builder()
                .eventId(record.getEventId())
                .eventType(record.getEventType())
                .aggregateType(record.getAggregateType())
                .aggregateId(record.getAggregateId())
                .aggregateVersion(record.getAggregateVersion())
                .topic(record.getTopic())
                .messageKey(record.getMessageKey())
                .payload(record.getPayload())
                .publishState(record.getPublishState())
                .retryCount(record.getRetryCount() == null ? 0 : record.getRetryCount())
                .build());
    }

    @Override
    public List<OutboxRecord> listPublishable(Date now, int limit) {
        return tmsOutboxDao.selectPublishable(now, limit).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int markPublished(Long id) {
        return tmsOutboxDao.markPublished(id);
    }

    @Override
    public int markRetryable(Long id, int retryCount, Date nextRetryTime, String error) {
        return tmsOutboxDao.markRetryable(id, retryCount, nextRetryTime, error);
    }

    @Override
    public int markDead(Long id, String error) {
        return tmsOutboxDao.markDead(id, error);
    }

    private OutboxRecord toDomain(TmsOutboxPo po) {
        return OutboxRecord.builder()
                .id(po.getId())
                .eventId(po.getEventId())
                .eventType(po.getEventType())
                .aggregateType(po.getAggregateType())
                .aggregateId(po.getAggregateId())
                .aggregateVersion(po.getAggregateVersion())
                .topic(po.getTopic())
                .messageKey(po.getMessageKey())
                .payload(po.getPayload())
                .publishState(po.getPublishState())
                .retryCount(po.getRetryCount())
                .nextRetryTime(po.getNextRetryTime())
                .publishedAt(po.getPublishedAt())
                .lastError(po.getLastError())
                .build();
    }
}
