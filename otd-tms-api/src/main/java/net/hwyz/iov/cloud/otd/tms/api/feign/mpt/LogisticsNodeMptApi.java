package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;

import java.util.List;
import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeMpt;

/**
 * 物流据点相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface LogisticsNodeMptApi {

    /**
     * 分页查询物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 物流据点信息列表
     */
    ApiResponse<PageResult<LogisticsNodeMpt>> list(LogisticsNodeMpt logisticsNode);

    /**
     * 查询所有LogisticsNodeMpt（下拉选项用）
     *
     * @return 列表
     */
    List<LogisticsNodeMpt> listAll();

    /**
     * 导出物流据点信息
     *
     * @param response      响应
     * @param logisticsNode 物流据点信息
     */
    void export(HttpServletResponse response, LogisticsNodeMpt logisticsNode);

    /**
     * 根据物流据点ID获取物流据点信息
     *
     * @param logisticsNodeId 物流据点ID
     * @return 物流据点类型
     */
    AjaxResult getInfo(Long logisticsNodeId);

    /**
     * 新增物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 结果
     */
    AjaxResult add(LogisticsNodeMpt logisticsNode);

    /**
     * 修改保存物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 结果
     */
    AjaxResult edit(LogisticsNodeMpt logisticsNode);

    /**
     * 删除物流据点信息
     *
     * @param logisticsNodeIds 物流据点ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] logisticsNodeIds);

}
