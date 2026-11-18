package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.DriverPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 驾驶员 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Mapper
public interface DriverDao extends BaseDao<DriverPo, Long> {

    /**
     * 通过身份证号查询驾驶员
     *
     * @param idNumber 身份证号
     * @return 驾驶员
     */
    DriverPo selectPoByIdNumber(String idNumber);

}
