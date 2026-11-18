package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportOrderDetailPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 运输订单明细 DAO
 * </p>
 *
 * @author hwyz_leo
 */
@Mapper
public interface TransportOrderDetailDao extends BaseDao<TransportOrderDetailPo, Long> {

    List<TransportOrderDetailPo> selectPoByOrderNum(@Param("orderNum") String orderNum);

    int batchInsertPo(@Param("list") List<TransportOrderDetailPo> entities);

    int updatePo(TransportOrderDetailPo entity);

    int deleteByOrderNum(@Param("orderNum") String orderNum);
}
