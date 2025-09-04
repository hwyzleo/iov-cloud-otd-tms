package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 物流据点类型 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-13
 */
@Mapper
public interface LogisticsNodeTypeDao extends BaseDao<LogisticsNodeTypePo, Long> {

    /**
     * 通过code查询物流据点类型
     *
     * @param code 物流据点类型编码
     * @return 物流据点类型
     */
    LogisticsNodeTypePo selectPoByCode(String code);

}
