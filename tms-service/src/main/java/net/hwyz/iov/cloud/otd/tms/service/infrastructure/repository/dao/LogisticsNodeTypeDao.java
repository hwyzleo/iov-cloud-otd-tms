package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
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

    /**
     * 批量物理删除物流据点类型
     *
     * @param ids 物流据点类型id数组
     * @return 影响行数
     */
    int batchPhysicalDeletePo(Long[] ids);

}
