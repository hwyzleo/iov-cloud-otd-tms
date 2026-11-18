package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.InboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsInboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TmsInboxDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TmsInboxPo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 入站消息去重仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class TmsInboxRepositoryImpl implements TmsInboxRepository {

    private final TmsInboxDao tmsInboxDao;

    @Override
    public Optional<InboxRecord> find(String consumerName, String topic, String eventId) {
        TmsInboxPo po = tmsInboxDao.selectPoByConsumerAndEvent(consumerName, eventId);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(InboxRecord.builder()
                .id(po.getId())
                .consumerName(po.getConsumerName())
                .topic(po.getTopic())
                .eventId(po.getEventId())
                .eventType(po.getEventType())
                .payloadDigest(po.getPayloadDigest())
                .processState(po.getProcessState())
                .businessKey(po.getBusinessKey())
                .resultEventId(po.getResultEventId())
                .errorCode(po.getErrorCode())
                .errorMessage(po.getErrorMessage())
                .receivedTime(po.getReceivedTime())
                .processedTime(po.getProcessedTime())
                .build());
    }

    @Override
    public void insert(InboxRecord record) {
        tmsInboxDao.insertPo(TmsInboxPo.builder()
                .consumerName(record.getConsumerName())
                .topic(record.getTopic())
                .eventId(record.getEventId())
                .eventType(record.getEventType())
                .payloadDigest(record.getPayloadDigest())
                .processState(record.getProcessState())
                .businessKey(record.getBusinessKey())
                .resultEventId(record.getResultEventId())
                .errorCode(record.getErrorCode())
                .errorMessage(record.getErrorMessage())
                .receivedTime(record.getReceivedTime())
                .processedTime(record.getProcessedTime())
                .build());
    }
}
