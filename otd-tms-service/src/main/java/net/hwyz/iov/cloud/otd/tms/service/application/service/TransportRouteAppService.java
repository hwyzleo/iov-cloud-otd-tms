package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportRouteDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输线路应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransportRouteAppService {

    private final TransportRouteDao transportRouteDao;

    /**
     * 查询运输线路
     *
     * @param carrierCode 承运商代码
     * @param code        运输线路代码
     * @param name        运输线路名称
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @return 物流据点类型列表
     */
    public List<TransportRoutePo> search(String carrierCode, String code, String name, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("carrierCode", carrierCode);
        map.put("code", code);
        map.put("name", ParamHelper.fuzzyQueryParam(name));
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return transportRouteDao.selectPoByMap(map);
    }

    /**
     * 检查运输线路代码是否唯一
     *
     * @param transportRouteId 运输线路ID
     * @param code             运输线路代码
     * @return 结果
     */
    public Boolean checkCodeUnique(Long transportRouteId, String code) {
        if (ObjUtil.isNull(transportRouteId)) {
            transportRouteId = -1L;
        }
        TransportRoutePo transportRoutePo = getTransportRouteByCode(code);
        return !ObjUtil.isNotNull(transportRoutePo) || transportRoutePo.getId().longValue() == transportRouteId.longValue();
    }

    /**
     * 根据主键ID获取运输线路
     *
     * @param id 主键ID
     * @return 运输线路
     */
    public TransportRoutePo getTransportRouteById(Long id) {
        return transportRouteDao.selectPoById(id);
    }

    /**
     * 根据运输线路代码获取运输线路
     *
     * @param code 运输线路代码
     * @return 运输线路
     */
    public TransportRoutePo getTransportRouteByCode(String code) {
        return transportRouteDao.selectPoByCode(code);
    }

    /**
     * 新增运输线路
     *
     * @param transportRoutePo 运输线路
     * @return 结果
     */
    public int createTransportRoute(TransportRoutePo transportRoutePo) {
        return transportRouteDao.insertPo(transportRoutePo);
    }

    /**
     * 修改运输线路
     *
     * @param transportRoutePo 运输线路
     * @return 结果
     */
    public int modifyTransportType(TransportRoutePo transportRoutePo) {
        return transportRouteDao.updatePo(transportRoutePo);
    }

    /**
     * 批量删除运输线路
     *
     * @param ids 运输线路ID数组
     * @return 结果
     */
    public int deleteTransportRouteByIds(Long[] ids) {
        return transportRouteDao.batchPhysicalDeletePo(ids);
    }

}
