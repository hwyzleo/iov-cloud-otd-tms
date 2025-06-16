package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.otd.tms.api.contract.DriverMpt;

/**
 * 驾驶员相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface DriverMptApi {

    /**
     * 分页查询驾驶员
     *
     * @param driver 承运商
     * @return 承运商列表
     */
    TableDataInfo list(DriverMpt driver);

    /**
     * 导出驾驶员
     *
     * @param response 响应
     * @param driver   驾驶员
     */
    void export(HttpServletResponse response, DriverMpt driver);

    /**
     * 根据驾驶员ID获取驾驶员
     *
     * @param driverId 驾驶员ID
     * @return 驾驶员
     */
    AjaxResult getInfo(Long driverId);

    /**
     * 新增驾驶员
     *
     * @param driver 驾驶员
     * @return 结果
     */
    AjaxResult add(DriverMpt driver);

    /**
     * 修改保存驾驶员
     *
     * @param driver 驾驶员
     * @return 结果
     */
    AjaxResult edit(DriverMpt driver);

    /**
     * 删除驾驶员
     *
     * @param driverIds 驾驶员ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] driverIds);

}
