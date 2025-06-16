package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportTypePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 运输方式 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Mapper
public interface TransportTypeDao extends BaseDao<TransportTypePo, Long> {

    /**
     * 通过code查询运输方式
     *
     * @param code 运输方式编码
     * @return 运输方式
     */
    TransportTypePo selectPoByCode(String code);

    /**
     * 批量物理删除运输方式
     *
     * @param ids 运输方式id数组
     * @return 影响行数
     */
    int batchPhysicalDeletePo(Long[] ids);

}
