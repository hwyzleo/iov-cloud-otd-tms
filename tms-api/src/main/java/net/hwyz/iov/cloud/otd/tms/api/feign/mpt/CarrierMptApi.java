package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.otd.tms.api.contract.CarrierMpt;

import java.util.List;

/**
 * 承运商相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface CarrierMptApi {

    /**
     * 分页查询承运商
     *
     * @param carrier 承运商
     * @return 承运商列表
     */
    TableDataInfo list(CarrierMpt carrier);

    /**
     * 查询所有承运商
     *
     * @return 承运商列表
     */
    List<CarrierMpt> listAll();

    /**
     * 导出承运商
     *
     * @param response 响应
     * @param carrier  承运商
     */
    void export(HttpServletResponse response, CarrierMpt carrier);

    /**
     * 根据承运商ID获取承运商
     *
     * @param carrierId 承运商ID
     * @return 承运商
     */
    AjaxResult getInfo(Long carrierId);

    /**
     * 新增承运商
     *
     * @param carrier 承运商
     * @return 结果
     */
    AjaxResult add(CarrierMpt carrier);

    /**
     * 修改保存承运商
     *
     * @param carrier 承运商
     * @return 结果
     */
    AjaxResult edit(CarrierMpt carrier);

    /**
     * 删除承运商
     *
     * @param carrierIds 承运商ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] carrierIds);

}
