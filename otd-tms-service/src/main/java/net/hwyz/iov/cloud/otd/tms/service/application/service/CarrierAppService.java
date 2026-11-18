package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.CarrierDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportTypePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 承运商应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarrierAppService {

    private final CarrierDao carrierDao;

    /**
     * 查询承运商
     *
     * @param code      承运商代码
     * @param name      承运商名称
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 承运商列表
     */
    public List<CarrierPo> search(String code, String name, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("name", ParamHelper.fuzzyQueryParam(name));
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return carrierDao.selectPoByMap(map);
    }

    /**
     * 检查承运商代码是否唯一
     *
     * @param carrierId 承运商ID
     * @param code      承运商代码
     * @return 结果
     */
    public Boolean checkCodeUnique(Long carrierId, String code) {
        if (ObjUtil.isNull(carrierId)) {
            carrierId = -1L;
        }
        CarrierPo carrierPo = getCarrierByCode(code);
        return !ObjUtil.isNotNull(carrierPo) || carrierPo.getId().longValue() == carrierId.longValue();
    }

    /**
     * 根据主键ID获取承运商
     *
     * @param id 主键ID
     * @return 承运商
     */
    public CarrierPo getCarrierById(Long id) {
        return carrierDao.selectPoById(id);
    }

    /**
     * 根据承运商代码获取承运商
     *
     * @param code 承运商代码
     * @return 承运商
     */
    public CarrierPo getCarrierByCode(String code) {
        return carrierDao.selectPoByCode(code);
    }

    /**
     * 新增承运商
     *
     * @param carrierPo 承运商
     * @return 结果
     */
    public int createCarrier(CarrierPo carrierPo) {
        return carrierDao.insertPo(carrierPo);
    }

    /**
     * 修改承运商
     *
     * @param carrierPo 承运商
     * @return 结果
     */
    public int modifyCarrier(CarrierPo carrierPo) {
        return carrierDao.updatePo(carrierPo);
    }

    /**
     * 批量删除承运商
     *
     * @param ids 承运商ID数组
     * @return 结果
     */
    public int deleteCarrierByIds(Long[] ids) {
        return carrierDao.batchPhysicalDeletePo(ids);
    }

}
