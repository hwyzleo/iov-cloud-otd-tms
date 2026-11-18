package net.hwyz.iov.cloud.otd.tms.service.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;
import net.hwyz.iov.cloud.otd.tms.service.application.port.VsoOrderReportPort;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsErrorCode;
import net.hwyz.iov.cloud.otd.vso.api.service.VsoOrderService;
import net.hwyz.iov.cloud.otd.vso.api.vo.request.OrderStateChangeRequest;
import net.hwyz.iov.cloud.otd.vso.api.vo.response.TransportOrderBriefResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 订单状态回写适配器（Feign → OTD-VSO，OTD-TMS-DSN-CR-001 §1.5）
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VsoOrderReportAdapter implements VsoOrderReportPort {

    private final VsoOrderService vsoOrderService;

    @Override
    public void prepareTransport(String orderNo) {
        call("prepareTransport", orderNo, () -> vsoOrderService.prepareTransport(request(orderNo)));
    }

    @Override
    public void transporting(String orderNo) {
        call("transporting", orderNo, () -> vsoOrderService.transporting(request(orderNo)));
    }

    @Override
    public void prepareDelivery(String orderNo) {
        call("prepareDelivery", orderNo, () -> vsoOrderService.prepareDelivery(request(orderNo)));
    }

    @Override
    public List<VsoTransportAppliedOrder> findTransportApplied(String deliveryRegionCode, int pageNum, int pageSize) {
        try {
            PageResult<TransportOrderBriefResponse> page = vsoOrderService.listTransportApplied(
                    470, deliveryRegionCode, pageNum, pageSize);
            if (page == null || page.getItems() == null) {
                return List.of();
            }
            return page.getItems().stream()
                    .map(item -> new VsoTransportAppliedOrder(item.getOrderNo(), item.getVin(),
                            item.getDeliveryStoreCode(), item.getDeliveryRegionCode()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TmsBusinessException(TmsErrorCode.VSO_REPORT_FAILED,
                    "拉取待发运订单失败: " + e.getMessage());
        }
    }

    private void call(String action, String orderNo, Supplier<AjaxResult> invocation) {
        try {
            AjaxResult result = invocation.get();
            // 平台业务失败以 200 + code 表达，必须校验 isSuccess()，否则会把「状态机拒绝」当成功（联调发现）
            if (result == null || !result.isSuccess()) {
                String code = result == null ? "null" : String.valueOf(result.get(AjaxResult.CODE_TAG));
                String message = result == null ? "空响应" : String.valueOf(result.get(AjaxResult.MSG_TAG));
                throw new TmsBusinessException(TmsErrorCode.VSO_REPORT_FAILED,
                        "订单[" + orderNo + "]回写 VSO 被拒绝(" + action + "): " + code + " " + message);
            }
            log.info("订单[{}]回写 VSO 成功: {}", orderNo, action);
        } catch (TmsBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new TmsBusinessException(TmsErrorCode.VSO_REPORT_FAILED,
                    "订单[" + orderNo + "]回写 VSO 失败(" + action + "): " + e.getMessage());
        }
    }

    private OrderStateChangeRequest request(String orderNo) {
        return OrderStateChangeRequest.builder().orderNo(orderNo).build();
    }
}
