package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.LoadingVehicleDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LoadingVehiclePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装载车应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadingVehicleAppService {

    private final LoadingVehicleDao loadingVehicleDao;

    /**
     * 查询装载车
     *
     * @param carrierCode               承运商代码
     * @param licensePlate              车牌
     * @param vehicleRegistrationNumber 行驶证号
     * @param beginTime                 开始时间
     * @param endTime                   结束时间
     * @return 驾驶员列表
     */
    public List<LoadingVehiclePo> search(String carrierCode, String licensePlate, String vehicleRegistrationNumber,
                                         Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("carrierCode", carrierCode);
        map.put("licensePlate", ParamHelper.fuzzyQueryParam(licensePlate));
        map.put("vehicleRegistrationNumber", vehicleRegistrationNumber);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return loadingVehicleDao.selectPoByMap(map);
    }

    /**
     * 检查行驶证号是否唯一
     *
     * @param loadingVehicleId          装载车ID
     * @param vehicleRegistrationNumber 行驶证号
     * @return 结果
     */
    public Boolean checkVehicleRegistrationNumberUnique(Long loadingVehicleId, String vehicleRegistrationNumber) {
        if (ObjUtil.isNull(loadingVehicleId)) {
            loadingVehicleId = -1L;
        }
        LoadingVehiclePo loadVehiclePo = getDriverByVehicleRegistrationNumber(vehicleRegistrationNumber);
        return !ObjUtil.isNotNull(loadVehiclePo) || loadVehiclePo.getId().longValue() == loadingVehicleId.longValue();
    }

    /**
     * 根据主键ID获取装载车
     *
     * @param id 主键ID
     * @return 装载车
     */
    public LoadingVehiclePo getLoadingVehicleById(Long id) {
        return loadingVehicleDao.selectPoById(id);
    }

    /**
     * 根据行驶证号获取装载车
     *
     * @param vehicleRegistrationNumber 行驶证号
     * @return 装载车
     */
    public LoadingVehiclePo getDriverByVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        return loadingVehicleDao.selectPoByVehicleRegistrationNumber(vehicleRegistrationNumber);
    }

    /**
     * 新增装载车
     *
     * @param loadingVehiclePo 装载车
     * @return 结果
     */
    public int createLoadingVehicle(LoadingVehiclePo loadingVehiclePo) {
        return loadingVehicleDao.insertPo(loadingVehiclePo);
    }

    /**
     * 修改装载车
     *
     * @param loadingVehiclePo 装载车
     * @return 结果
     */
    public int modifyLoadingVehicle(LoadingVehiclePo loadingVehiclePo) {
        return loadingVehicleDao.updatePo(loadingVehiclePo);
    }

    /**
     * 批量删除装载车
     *
     * @param ids 装载车ID数组
     * @return 结果
     */
    public int deleteLoadingVehicleByIds(Long[] ids) {
        return loadingVehicleDao.batchPhysicalDeletePo(ids);
    }

}
