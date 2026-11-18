package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 运输线路 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Mapper
public interface TransportRouteDao extends BaseDao<TransportRoutePo, Long> {

    /**
     * 通过code查询运输线路
     *
     * @param code 运输线路编码
     * @return 运输线路
     */
    TransportRoutePo selectPoByCode(String code);

}
