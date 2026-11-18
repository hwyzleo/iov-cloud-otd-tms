package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.util.ParamHelper;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.LogisticsNodeDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流据点应用服务类
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsNodeAppService {

    private final LogisticsNodeDao logisticsNodeDao;

    /**
     * 查询物流据点信息
     *
     * @param code      物流据点类型代码
     * @param name      物流据点类型名称
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 物流据点类型列表
     */
    public List<LogisticsNodePo> search(String code, String name, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("name", ParamHelper.fuzzyQueryParam(name));
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return logisticsNodeDao.selectPoByMap(map);
    }

    /**
     * 检查物流据点类型代码是否唯一
     *
     * @param logisticsNodeId 物流据点类型ID
     * @param code            物流据点类型代码
     * @return 结果
     */
    public Boolean checkCodeUnique(Long logisticsNodeId, String code) {
        if (ObjUtil.isNull(logisticsNodeId)) {
            logisticsNodeId = -1L;
        }
        LogisticsNodePo logisticsNodePo = getLogisticsNodeByCode(code);
        return !ObjUtil.isNotNull(logisticsNodePo) || logisticsNodePo.getId().longValue() == logisticsNodeId.longValue();
    }

    /**
     * 根据主键ID获取物流据点信息
     *
     * @param id 主键ID
     * @return 物流据点信息
     */
    public LogisticsNodePo getLogisticsNodeById(Long id) {
        return logisticsNodeDao.selectPoById(id);
    }

    /**
     * 根据物流据点代码获取物流据点信息
     *
     * @param code 物流据点代码
     * @return 物流据点信息
     */
    public LogisticsNodePo getLogisticsNodeByCode(String code) {
        return logisticsNodeDao.selectPoByCode(code);
    }

    /**
     * 新增物流据点信息
     *
     * @param LogisticsNode 物流据点信息
     * @return 结果
     */
    public int createLogisticsNode(LogisticsNodePo LogisticsNode) {
        return logisticsNodeDao.insertPo(LogisticsNode);
    }

    /**
     * 修改物流据点信息
     *
     * @param LogisticsNode 物流据点信息
     * @return 结果
     */
    public int modifyLogisticsNode(LogisticsNodePo LogisticsNode) {
        return logisticsNodeDao.updatePo(LogisticsNode);
    }

    /**
     * 批量删除物流据点信息
     *
     * @param ids 物流据点ID数组
     * @return 结果
     */
    public int deleteLogisticsNodeByIds(Long[] ids) {
        return logisticsNodeDao.batchPhysicalDeletePo(ids);
    }

}
