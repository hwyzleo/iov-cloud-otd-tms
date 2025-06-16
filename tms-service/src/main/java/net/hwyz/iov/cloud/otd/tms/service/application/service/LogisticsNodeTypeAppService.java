package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.LogisticsNodeTypeDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流据点类型应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsNodeTypeAppService {

    private final LogisticsNodeTypeDao logisticsNodeTypeDao;

    /**
     * 查询物流据点类型
     *
     * @param code      物流据点类型代码
     * @param name      物流据点类型名称
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 物流据点类型列表
     */
    public List<LogisticsNodeTypePo> search(String code, String name, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("name", ParamHelper.fuzzyQueryParam(name));
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return logisticsNodeTypeDao.selectPoByMap(map);
    }

    /**
     * 检查物流据点类型代码是否唯一
     *
     * @param logisticsNodeTypeId 物流据点类型ID
     * @param code                物流据点类型代码
     * @return 结果
     */
    public Boolean checkCodeUnique(Long logisticsNodeTypeId, String code) {
        if (ObjUtil.isNull(logisticsNodeTypeId)) {
            logisticsNodeTypeId = -1L;
        }
        LogisticsNodeTypePo logisticsNodeTypePo = getLogisticsNodeTypeByCode(code);
        return !ObjUtil.isNotNull(logisticsNodeTypePo) || logisticsNodeTypePo.getId().longValue() == logisticsNodeTypeId.longValue();
    }

    /**
     * 根据主键ID获取物流据点类型
     *
     * @param id 主键ID
     * @return 物流据点类型
     */
    public LogisticsNodeTypePo getLogisticsNodeTypeById(Long id) {
        return logisticsNodeTypeDao.selectPoById(id);
    }

    /**
     * 根据物流据点类型代码获取物流据点类型信息
     *
     * @param code 物流据点类型代码
     * @return 物流据点类型信息
     */
    public LogisticsNodeTypePo getLogisticsNodeTypeByCode(String code) {
        return logisticsNodeTypeDao.selectPoByCode(code);
    }

    /**
     * 新增物流据点类型
     *
     * @param LogisticsNodeType 物流据点类型
     * @return 结果
     */
    public int createLogisticsNodeType(LogisticsNodeTypePo LogisticsNodeType) {
        return logisticsNodeTypeDao.insertPo(LogisticsNodeType);
    }

    /**
     * 修改物流据点类型
     *
     * @param LogisticsNodeType 物流据点类型
     * @return 结果
     */
    public int modifyLogisticsNodeType(LogisticsNodeTypePo LogisticsNodeType) {
        return logisticsNodeTypeDao.updatePo(LogisticsNodeType);
    }

    /**
     * 批量删除物流据点类型
     *
     * @param ids 物流据点类型ID数组
     * @return 结果
     */
    public int deleteLogisticsNodeTypeByIds(Long[] ids) {
        return logisticsNodeTypeDao.batchPhysicalDeletePo(ids);
    }

}
