package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.otd.tms.api.contract.LoadingVehicleMpt;

/**
 * 装载车相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface LoadingVehicleMptApi {

    /**
     * 分页查询装载车
     *
     * @param loadingVehicle 装载车
     * @return 装载车列表
     */
    TableDataInfo list(LoadingVehicleMpt loadingVehicle);

    /**
     * 导出装载车
     *
     * @param response       响应
     * @param loadingVehicle 装载车
     */
    void export(HttpServletResponse response, LoadingVehicleMpt loadingVehicle);

    /**
     * 根据装载车ID获取装载车
     *
     * @param loadingVehicleId 装载车ID
     * @return 装载车
     */
    AjaxResult getInfo(Long loadingVehicleId);

    /**
     * 新增装载车
     *
     * @param loadingVehicle 装载车
     * @return 结果
     */
    AjaxResult add(LoadingVehicleMpt loadingVehicle);

    /**
     * 修改保存装载车
     *
     * @param loadingVehicle 装载车
     * @return 结果
     */
    AjaxResult edit(LoadingVehicleMpt loadingVehicle);

    /**
     * 删除装载车
     *
     * @param loadingVehicleIds 装载车ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] loadingVehicleIds);

}
