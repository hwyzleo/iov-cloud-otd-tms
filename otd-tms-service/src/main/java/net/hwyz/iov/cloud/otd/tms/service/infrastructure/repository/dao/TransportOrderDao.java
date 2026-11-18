package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportOrderPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运输订单 DAO
 * </p>
 *
 * @author hwyz_leo
 */
@Mapper
public interface TransportOrderDao extends BaseDao<TransportOrderPo, Long> {

    TransportOrderPo selectPoByOrderNum(@Param("orderNum") String orderNum);

    TransportOrderPo selectPoBySourceOrder(@Param("sourceSystem") String sourceSystem,
                                           @Param("sourceOrderNo") String sourceOrderNo);

    List<TransportOrderPo> selectPoByMap(Map<String, Object> params);

    List<String> selectOrderNumsByStates(@Param("states") List<Integer> states, @Param("limit") int limit);

    int countActiveByVin(@Param("vin") String vin, @Param("orderNum") String orderNum);

    int insertPo(TransportOrderPo entity);

    int updatePo(TransportOrderPo entity);
}
