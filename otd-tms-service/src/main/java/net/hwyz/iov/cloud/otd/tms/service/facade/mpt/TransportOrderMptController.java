package net.hwyz.iov.cloud.otd.tms.service.facade.mpt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.audit.annotation.Log;
import net.hwyz.iov.cloud.framework.audit.enums.BusinessType;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;
import net.hwyz.iov.cloud.framework.security.annotation.RequiresPermissions;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import net.hwyz.iov.cloud.framework.web.controller.BaseController;
import net.hwyz.iov.cloud.framework.web.util.PageUtil;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderActionMpt;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderDetailMpt;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.TransportOrderMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportOrderAppService;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderState;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.TransportOrderMptAssembler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运输单相关管理接口实现类（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/transportOrder")
public class TransportOrderMptController extends BaseController implements TransportOrderMptApi {

    private final TransportOrderAppService transportOrderAppService;

    @RequiresPermissions("completeVehicle:transport:transportOrder:list")
    @Override
    @GetMapping(value = "/list")
    public ApiResponse<PageResult<TransportOrderMpt>> list(TransportOrderMpt transportOrder) {
        log.info("管理后台用户[{}]分页查询运输单", SecurityUtils.getUsername());
        startPage();
        List<TransportOrder> orders = transportOrderAppService.search(
                transportOrder.getOrderNum(),
                TransportOrderState.fromCode(transportOrder.getState()),
                transportOrder.getCarrierCode(),
                transportOrder.getDispatchLogisticsNodeCode(),
                getBeginTime(transportOrder), getEndTime(transportOrder));
        return ApiResponse.ok(getPageResult(PageUtil.convert(orders, TransportOrderMptAssembler.INSTANCE::fromDomain)));
    }

    @RequiresPermissions("completeVehicle:transport:transportOrder:query")
    @Override
    @GetMapping(value = "/{orderNum}")
    public AjaxResult getInfo(@PathVariable String orderNum) {
        log.info("管理后台用户[{}]查询运输单[{}]", SecurityUtils.getUsername(), orderNum);
        return success(TransportOrderMptAssembler.INSTANCE.fromDomain(
                transportOrderAppService.getByOrderNum(orderNum)));
    }

    @RequiresPermissions("completeVehicle:transport:transportOrder:query")
    @Override
    @GetMapping(value = "/{orderNum}/detail")
    public List<TransportOrderDetailMpt> listDetail(@PathVariable String orderNum) {
        log.info("管理后台用户[{}]查询运输单[{}] VIN 明细", SecurityUtils.getUsername(), orderNum);
        return TransportOrderMptAssembler.INSTANCE.fromDetailList(
                transportOrderAppService.getByOrderNum(orderNum).getDetails());
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:dispatch")
    @Override
    @PostMapping("/action/dispatch")
    public AjaxResult dispatch(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]调度运输单[{}]", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.dispatch(new TransportOrderAppService.DispatchCommand(
                request.getOrderNum(), request.getCarrierCode(), request.getTransportRouteCode(),
                request.getDispatchLogisticsNodeCode(), request.getHandoverLogisticsNodeCode(),
                request.getLicensePlate(), request.getIdNumber(), request.getEstimatedLoadingTime(),
                request.getEstimatedArrivalTime(), request.getDispatcher()));
        return success();
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:depart")
    @Override
    @PostMapping("/action/depart")
    public AjaxResult depart(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]确认发车运输单[{}]", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.depart(request.getOrderNum(), SecurityUtils.getUsername());
        return success();
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:handover")
    @Override
    @PostMapping("/action/handover")
    public AjaxResult handover(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]运输单[{}]交车", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.handover(request.getOrderNum(), Boolean.TRUE.equals(request.getAllCompleted()),
                SecurityUtils.getUsername());
        return success();
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:receipt")
    @Override
    @PostMapping("/action/receipt")
    public AjaxResult receipt(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]运输单[{}]接车", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.receipt(request.getOrderNum(), Boolean.TRUE.equals(request.getAllCompleted()),
                SecurityUtils.getUsername());
        return success();
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:cancel")
    @Override
    @PostMapping("/action/cancel")
    public AjaxResult cancel(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]取消运输单[{}]", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.cancel(request.getOrderNum(), request.getReason(), SecurityUtils.getUsername());
        return success();
    }

    @Log(title = "运输单管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportOrder:manualClose")
    @Override
    @PostMapping("/action/manualClose")
    public AjaxResult manualClose(@Valid @RequestBody TransportOrderActionMpt request) {
        log.info("管理后台用户[{}]手工结束运输单[{}]", SecurityUtils.getUsername(), request.getOrderNum());
        transportOrderAppService.manualClose(request.getOrderNum(), SecurityUtils.getUsername());
        return success();
    }
}
