package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;

import java.util.List;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportRouteMpt;

/**
 * 运输线路相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface TransportRouteMptApi {

    /**
     * 分页查询运输线路
     *
     * @param transportRoute 运输线路
     * @return 运输线路列表
     */
    ApiResponse<PageResult<TransportRouteMpt>> list(TransportRouteMpt transportRoute);

    /**
     * 查询所有TransportRouteMpt（下拉选项用）
     *
     * @return 列表
     */
    List<TransportRouteMpt> listAll();

    /**
     * 导出运输线路
     *
     * @param response       响应
     * @param transportRoute 运输线路
     */
    void export(HttpServletResponse response, TransportRouteMpt transportRoute);

    /**
     * 根据运输线路ID获取运输线路
     *
     * @param transportRouteId 运输线路ID
     * @return 运输线路
     */
    AjaxResult getInfo(Long transportRouteId);

    /**
     * 新增运输线路
     *
     * @param transportRoute 运输线路
     * @return 结果
     */
    AjaxResult add(TransportRouteMpt transportRoute);

    /**
     * 修改保存运输线路
     *
     * @param transportRoute 运输线路
     * @return 结果
     */
    AjaxResult edit(TransportRouteMpt transportRoute);

    /**
     * 删除运输线路
     *
     * @param transportRouteIds 运输线路ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] transportRouteIds);

}
