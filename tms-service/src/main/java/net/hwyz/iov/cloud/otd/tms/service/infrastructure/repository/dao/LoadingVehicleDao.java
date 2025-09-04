package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao;

import net.hwyz.iov.cloud.framework.mysql.dao.BaseDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LoadingVehiclePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 装载车 DAO
 * </p>
 *
 * @author hwyz_leo
 * @since 2025-06-16
 */
@Mapper
public interface LoadingVehicleDao extends BaseDao<LoadingVehiclePo, Long> {

    /**
     * 通过行驶证号查询装载车
     *
     * @param vehicleRegistrationNumber 行驶证号
     * @return 装载车
     */
    LoadingVehiclePo selectPoByVehicleRegistrationNumber(String vehicleRegistrationNumber);

}
