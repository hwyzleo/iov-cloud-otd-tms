package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 承运商 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Mapper
public interface CarrierDao extends BaseDao<CarrierPo, Long> {

    /**
     * 通过code查询承运商
     *
     * @param code 承运商编码
     * @return 承运商
     */
    CarrierPo selectPoByCode(String code);

}
