package net.hwyz.iov.cloud.otd.tms.api.feign.mpt;

import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderActionMpt;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderDetailMpt;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderMpt;

import java.util.List;

/**
 * 运输单相关管理后台接口（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
public interface TransportOrderMptApi {

    /**
     * 分页查询运输单
     *
     * @param transportOrder 运输单查询条件
     * @return 运输单列表
     */
    ApiResponse<PageResult<TransportOrderMpt>> list(TransportOrderMpt transportOrder);

    /**
     * 根据运输单号获取运输单（含 VIN 明细）
     *
     * @param orderNum 运输单号
     * @return 运输单
     */
    AjaxResult getInfo(String orderNum);

    /**
     * 查询运输单 VIN 明细（含 WMS 状态快照）
     *
     * @param orderNum 运输单号
     * @return VIN 明细列表
     */
    List<TransportOrderDetailMpt> listDetail(String orderNum);

    /**
     * 调度（生成出库指令并回写订单状态 500）
     *
     * @param request 调度请求
     * @return 结果
     */
    AjaxResult dispatch(TransportOrderActionMpt request);

    /**
     * 确认发车（要求全部 VIN 已出库，回写订单状态 550）
     *
     * @param request 请求
     * @return 结果
     */
    AjaxResult depart(TransportOrderActionMpt request);

    /**
     * 交车（首次全部交车回写订单状态 600）
     *
     * @param request 请求
     * @return 结果
     */
    AjaxResult handover(TransportOrderActionMpt request);

    /**
     * 接车
     *
     * @param request 请求
     * @return 结果
     */
    AjaxResult receipt(TransportOrderActionMpt request);

    /**
     * 取消运单（仅未装车可取消）
     *
     * @param request 请求
     * @return 结果
     */
    AjaxResult cancel(TransportOrderActionMpt request);

    /**
     * 手工结束
     *
     * @param request 请求
     * @return 结果
     */
    AjaxResult manualClose(TransportOrderActionMpt request);
}
