package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.DriverDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.DriverPo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 驾驶员应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DriverAppService {

    private final DriverDao driverDao;

    /**
     * 查询驾驶员
     *
     * @param carrierCode 承运商代码
     * @param realName    驾驶员姓名
     * @param idNumber    身份证号
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @return 驾驶员列表
     */
    public List<DriverPo> search(String carrierCode, String realName, String idNumber, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("carrierCode", carrierCode);
        map.put("realName", ParamHelper.fuzzyQueryParam(realName));
        map.put("idNumber", idNumber);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return driverDao.selectPoByMap(map);
    }

    /**
     * 检查身份证号是否唯一
     *
     * @param driverId 驾驶员ID
     * @param idNumber 身份证号
     * @return 结果
     */
    public Boolean checkIdNumberUnique(Long driverId, String idNumber) {
        if (ObjUtil.isNull(driverId)) {
            driverId = -1L;
        }
        DriverPo driverPo = getDriverByIdNumber(idNumber);
        return !ObjUtil.isNotNull(driverPo) || driverPo.getId().longValue() == driverId.longValue();
    }

    /**
     * 根据主键ID获取驾驶员
     *
     * @param id 主键ID
     * @return 驾驶员
     */
    public DriverPo getDriverById(Long id) {
        return driverDao.selectPoById(id);
    }

    /**
     * 根据身份证号获取驾驶员
     *
     * @param idNumber 身份证号
     * @return 驾驶员
     */
    public DriverPo getDriverByIdNumber(String idNumber) {
        return driverDao.selectPoByIdNumber(idNumber);
    }

    /**
     * 新增驾驶员
     *
     * @param driverPo 驾驶员
     * @return 结果
     */
    public int createDriver(DriverPo driverPo) {
        return driverDao.insertPo(driverPo);
    }

    /**
     * 修改驾驶员
     *
     * @param driverPo 驾驶员
     * @return 结果
     */
    public int modifyDriver(DriverPo driverPo) {
        return driverDao.updatePo(driverPo);
    }

    /**
     * 批量删除驾驶员
     *
     * @param ids 驾驶员ID数组
     * @return 结果
     */
    public int deleteDriverByIds(Long[] ids) {
        return driverDao.batchPhysicalDeletePo(ids);
    }

}
