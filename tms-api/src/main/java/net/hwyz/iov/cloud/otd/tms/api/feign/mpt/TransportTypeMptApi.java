package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import jakarta.servlet.http.HttpServletResponse;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportTypeMpt;

import java.util.List;

/**
 * 运输方式相关管理后台接口
 *
 * @author hwyz_leo
 */
public interface TransportTypeMptApi {

    /**
     * 分页查询运输方式
     *
     * @param transportType 运输方式
     * @return 运输方式列表
     */
    TableDataInfo list(TransportTypeMpt transportType);

    /**
     * 查询所有运输方式
     *
     * @return 运输方式列表
     */
    List<TransportTypeMpt> listAll();

    /**
     * 导出运输方式
     *
     * @param response      响应
     * @param transportType 运输方式
     */
    void export(HttpServletResponse response, TransportTypeMpt transportType);

    /**
     * 根据运输方式ID获取运输方式
     *
     * @param transportTypeId 运输方式ID
     * @return 运输方式
     */
    AjaxResult getInfo(Long transportTypeId);

    /**
     * 新增运输方式
     *
     * @param transportType 运输方式
     * @return 结果
     */
    AjaxResult add(TransportTypeMpt transportType);

    /**
     * 修改保存运输方式
     *
     * @param transportType 运输方式
     * @return 结果
     */
    AjaxResult edit(TransportTypeMpt transportType);

    /**
     * 删除运输方式
     *
     * @param transportTypeIds 运输方式ID数组
     * @return 结果
     */
    AjaxResult remove(Long[] transportTypeIds);

}
