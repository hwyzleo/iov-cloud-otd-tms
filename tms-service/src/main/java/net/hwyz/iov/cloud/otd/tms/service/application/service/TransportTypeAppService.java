package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportTypeDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportTypePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输方式应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransportTypeAppService {

    private final TransportTypeDao transportTypeDao;

    /**
     * 查询运输方式
     *
     * @param code      运输方式代码
     * @param name      运输方式名称
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 物流据点类型列表
     */
    public List<TransportTypePo> search(String code, String name, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("name", ParamHelper.fuzzyQueryParam(name));
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return transportTypeDao.selectPoByMap(map);
    }

    /**
     * 检查运输方式代码是否唯一
     *
     * @param transportTypeId 运输方式ID
     * @param code            运输方式代码
     * @return 结果
     */
    public Boolean checkCodeUnique(Long transportTypeId, String code) {
        if (ObjUtil.isNull(transportTypeId)) {
            transportTypeId = -1L;
        }
        TransportTypePo transportTypePo = getTransportTypeByCode(code);
        return !ObjUtil.isNotNull(transportTypePo) || transportTypePo.getId().longValue() == transportTypeId.longValue();
    }

    /**
     * 根据主键ID获取运输方式
     *
     * @param id 主键ID
     * @return 运输方式
     */
    public TransportTypePo getTransportTypeById(Long id) {
        return transportTypeDao.selectPoById(id);
    }

    /**
     * 根据运输方式代码获取运输方式
     *
     * @param code 运输方式代码
     * @return 运输方式
     */
    public TransportTypePo getTransportTypeByCode(String code) {
        return transportTypeDao.selectPoByCode(code);
    }

    /**
     * 新增运输方式
     *
     * @param transportTypePo 运输方式
     * @return 结果
     */
    public int createTransportType(TransportTypePo transportTypePo) {
        return transportTypeDao.insertPo(transportTypePo);
    }

    /**
     * 修改运输方式
     *
     * @param transportTypePo 运输方式
     * @return 结果
     */
    public int modifyTransportType(TransportTypePo transportTypePo) {
        return transportTypeDao.updatePo(transportTypePo);
    }

    /**
     * 批量删除运输方式
     *
     * @param ids 运输方式ID数组
     * @return 结果
     */
    public int deleteTransportTypeByIds(Long[] ids) {
        return transportTypeDao.batchPhysicalDeletePo(ids);
    }

}
