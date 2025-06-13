package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 物流据点 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-13
 */
@Mapper
public interface LogisticsNodeDao extends BaseDao<LogisticsNodePo, Long> {

    /**
     * 通过code查询物流据点信息
     *
     * @param code 物流据点编码
     * @return 物流据点信息
     */
    LogisticsNodePo selectPoByCode(String code);

    /**
     * 批量物理删除物流据点信息
     *
     * @param ids 物流据点id数组
     * @return 影响行数
     */
    int batchPhysicalDeletePo(Long[] ids);

}
