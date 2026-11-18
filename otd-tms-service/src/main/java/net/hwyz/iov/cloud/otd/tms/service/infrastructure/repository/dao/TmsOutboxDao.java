package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TmsOutboxPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * TMS Outbox DAO
 * </p>
 *
 * @author hwyz_leo
 */
@Mapper
public interface TmsOutboxDao extends BaseDao<TmsOutboxPo, Long> {

    int insertPo(TmsOutboxPo entity);

    List<TmsOutboxPo> selectPublishable(@Param("now") Date now, @Param("limit") int limit);

    int markPublished(@Param("id") Long id);

    int markRetryable(@Param("id") Long id, @Param("retryCount") int retryCount,
                      @Param("nextRetryTime") Date nextRetryTime, @Param("error") String error);

    int markDead(@Param("id") Long id, @Param("error") String error);
}
