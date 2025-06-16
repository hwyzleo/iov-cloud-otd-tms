package net.hwyz.iov.cloud.otd.tms.service.facade.mpt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.audit.annotation.Log;
import net.hwyz.iov.cloud.framework.audit.enums.BusinessType;
import net.hwyz.iov.cloud.framework.common.web.controller.BaseController;
import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.framework.common.web.page.TableDataInfo;
import net.hwyz.iov.cloud.framework.security.annotation.RequiresPermissions;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import net.hwyz.iov.cloud.otd.tms.api.contract.CarrierMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.CarrierMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.CarrierAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.CarrierMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 承运商相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/carrier")
public class CarrierMptController extends BaseController implements CarrierMptApi {

    private final CarrierAppService carrierAppService;

    /**
     * 分页查询承运商
     *
     * @param carrier 承运商
     * @return 承运商列表
     */
    @RequiresPermissions("completeVehicle:transport:carrier:list")
    @Override
    @GetMapping(value = "/list")
    public TableDataInfo list(CarrierMpt carrier) {
        logger.info("管理后台用户[{}]分页查询承运商", SecurityUtils.getUsername());
        startPage();
        List<CarrierPo> carrierPoList = carrierAppService.search(carrier.getCode(), carrier.getName(),
                getBeginTime(carrier), getEndTime(carrier));
        List<CarrierMpt> carrierMptList = CarrierMptAssembler.INSTANCE.fromPoList(carrierPoList);
        return getDataTable(carrierPoList, carrierMptList);
    }

    /**
     * 查询所有承运商
     *
     * @return 承运商列表
     */
    @RequiresPermissions("completeVehicle:transport:carrier:list")
    @Override
    @GetMapping(value = "/listAll")
    public List<CarrierMpt> listAll() {
        logger.info("管理后台用户[{}]查询所有承运商", SecurityUtils.getUsername());
        List<CarrierPo> carrierPoList = carrierAppService.search(null, null, null, null);
        return CarrierMptAssembler.INSTANCE.fromPoList(carrierPoList);
    }

    /**
     * 导出承运商
     *
     * @param response 响应
     * @param carrier  承运商
     */
    @Log(title = "承运商管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:carrier:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, CarrierMpt carrier) {
        logger.info("管理后台用户[{}]导出承运商", SecurityUtils.getUsername());
    }

    /**
     * 根据承运商ID获取承运商
     *
     * @param carrierId 承运商ID
     * @return 承运商
     */
    @RequiresPermissions("completeVehicle:transport:carrier:query")
    @Override
    @GetMapping(value = "/{carrierId}")
    public AjaxResult getInfo(@PathVariable Long carrierId) {
        logger.info("管理后台用户[{}]根据承运商ID[{}]获取承运商", SecurityUtils.getUsername(), carrierId);
        CarrierPo carrierPo = carrierAppService.getCarrierById(carrierId);
        return success(CarrierMptAssembler.INSTANCE.fromPo(carrierPo));
    }

    /**
     * 新增承运商
     *
     * @param carrier 承运商
     * @return 结果
     */
    @Log(title = "承运商管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:carrier:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CarrierMpt carrier) {
        logger.info("管理后台用户[{}]新增承运商[{}]", SecurityUtils.getUsername(), carrier.getCode());
        if (!carrierAppService.checkCodeUnique(carrier.getId(), carrier.getCode())) {
            return error("新增承运商'" + carrier.getCode() + "'失败，承运商代码已存在");
        }
        CarrierPo carrierPo = CarrierMptAssembler.INSTANCE.toPo(carrier);
        carrierPo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(carrierAppService.createCarrier(carrierPo));
    }

    /**
     * 修改保存承运商
     *
     * @param carrier 承运商
     * @return 结果
     */
    @Log(title = "承运商管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:carrier:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CarrierMpt carrier) {
        logger.info("管理后台用户[{}]修改保存承运商[{}]", SecurityUtils.getUsername(), carrier.getCode());
        if (!carrierAppService.checkCodeUnique(carrier.getId(), carrier.getCode())) {
            return error("修改保存承运商'" + carrier.getCode() + "'失败，承运商代码已存在");
        }
        CarrierPo carrierPo = CarrierMptAssembler.INSTANCE.toPo(carrier);
        carrierPo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(carrierAppService.modifyCarrier(carrierPo));
    }

    /**
     * 删除承运商
     *
     * @param carrierIds 承运商ID数组
     * @return 结果
     */
    @Log(title = "承运商管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:carrier:remove")
    @Override
    @DeleteMapping("/{carrierIds}")
    public AjaxResult remove(@PathVariable Long[] carrierIds) {
        logger.info("管理后台用户[{}]删除承运商[{}]", SecurityUtils.getUsername(), carrierIds);
        return toAjax(carrierAppService.deleteCarrierByIds(carrierIds));
    }

}
