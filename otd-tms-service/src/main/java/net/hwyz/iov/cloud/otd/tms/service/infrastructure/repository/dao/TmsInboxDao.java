package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TmsInboxPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * TMS Inbox DAO
 * </p>
 *
 * @author hwyz_leo
 */
@Mapper
public interface TmsInboxDao extends BaseDao<TmsInboxPo, Long> {

    TmsInboxPo selectPoByConsumerAndEvent(@Param("consumerName") String consumerName,
                                          @Param("eventId") String eventId);

    int insertPo(TmsInboxPo entity);
}
