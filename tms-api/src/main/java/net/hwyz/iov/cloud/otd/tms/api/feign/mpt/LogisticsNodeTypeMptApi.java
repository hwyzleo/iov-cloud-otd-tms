package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeTypeMpt;

import java.util.List;

/**
 * 物流据点类型相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface LogisticsNodeTypeMptApi {

    /**
     * 分页查询物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 物流据点类型列表
     */
    TableDataInfo list(LogisticsNodeTypeMpt logisticsNodeType);

    /**
     * 查询所有物流据点类型
     *
     * @return 物流据点类型列表
     */
    List<LogisticsNodeTypeMpt> listAll();

    /**
     * 导出物流据点类型
     *
     * @param response          响应
     * @param logisticsNodeType 物流据点类型
     */
    void export(HttpServletResponse response, LogisticsNodeTypeMpt logisticsNodeType);

    /**
     * 根据物流据点类型ID获取物流据点类型
     *
     * @param logisticsNodeTypeId 物流据点类型ID
     * @return 物流据点类型
     */
    AjaxResult getInfo(Long logisticsNodeTypeId);

    /**
     * 新增物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 结果
     */
    AjaxResult add(LogisticsNodeTypeMpt logisticsNodeType);

    /**
     * 修改保存物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 结果
     */
    AjaxResult edit(LogisticsNodeTypeMpt logisticsNodeType);

    /**
     * 删除物流据点类型
     *
     * @param logisticsNodeTypeIds 物流据点类型ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] logisticsNodeTypeIds);

}
