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
import net.hwyz.iov.cloud.otd.tms.api.contract.DriverMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.DriverMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.DriverAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.DriverMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.DriverPo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 驾驶员相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/driver")
public class DriverMptController extends BaseController implements DriverMptApi {

    private final DriverAppService driverAppService;

    /**
     * 分页查询驾驶员
     *
     * @param driver 驾驶员
     * @return 驾驶员列表
     */
    @RequiresPermissions("completeVehicle:transport:driver:list")
    @Override
    @GetMapping(value = "/list")
    public TableDataInfo list(DriverMpt driver) {
        logger.info("管理后台用户[{}]分页查询驾驶员", SecurityUtils.getUsername());
        startPage();
        List<DriverPo> driverPoList = driverAppService.search(driver.getCarrierCode(), driver.getRealName(),
                driver.getIdNumber(), getBeginTime(driver), getEndTime(driver));
        List<DriverMpt> driverMptList = DriverMptAssembler.INSTANCE.fromPoList(driverPoList);
        return getDataTable(driverPoList, driverMptList);
    }

    /**
     * 导出驾驶员
     *
     * @param response 响应
     * @param driver   驾驶员
     */
    @Log(title = "驾驶员管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:driver:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, DriverMpt driver) {
        logger.info("管理后台用户[{}]导出驾驶员", SecurityUtils.getUsername());
    }

    /**
     * 根据驾驶员ID获取驾驶员
     *
     * @param driverId 驾驶员ID
     * @return 驾驶员
     */
    @RequiresPermissions("completeVehicle:transport:driver:query")
    @Override
    @GetMapping(value = "/{driverId}")
    public AjaxResult getInfo(@PathVariable Long driverId) {
        logger.info("管理后台用户[{}]根据驾驶员ID[{}]获取驾驶员", SecurityUtils.getUsername(), driverId);
        DriverPo driverPo = driverAppService.getDriverById(driverId);
        return success(DriverMptAssembler.INSTANCE.fromPo(driverPo));
    }

    /**
     * 新增驾驶员
     *
     * @param driver 驾驶员
     * @return 结果
     */
    @Log(title = "驾驶员管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:driver:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody DriverMpt driver) {
        logger.info("管理后台用户[{}]新增驾驶员[{}]", SecurityUtils.getUsername(), driver.getIdNumber());
        if (!driverAppService.checkIdNumberUnique(driver.getId(), driver.getIdNumber())) {
            return error("新增驾驶员'" + driver.getIdNumber() + "'失败，驾驶员身份证号已存在");
        }
        DriverPo driverPo = DriverMptAssembler.INSTANCE.toPo(driver);
        driverPo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(driverAppService.createDriver(driverPo));
    }

    /**
     * 修改保存驾驶员
     *
     * @param driver 驾驶员
     * @return 结果
     */
    @Log(title = "驾驶员管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:driver:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody DriverMpt driver) {
        logger.info("管理后台用户[{}]修改保存驾驶员[{}]", SecurityUtils.getUsername(), driver.getIdNumber());
        if (!driverAppService.checkIdNumberUnique(driver.getId(), driver.getIdNumber())) {
            return error("修改保存驾驶员'" + driver.getIdNumber() + "'失败，驾驶员身份证号已存在");
        }
        DriverPo driverPo = DriverMptAssembler.INSTANCE.toPo(driver);
        driverPo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(driverAppService.modifyDriver(driverPo));
    }

    /**
     * 删除驾驶员
     *
     * @param driverIds 驾驶员ID数组
     * @return 结果
     */
    @Log(title = "驾驶员管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:driver:remove")
    @Override
    @DeleteMapping("/{driverIds}")
    public AjaxResult remove(@PathVariable Long[] driverIds) {
        logger.info("管理后台用户[{}]删除驾驶员[{}]", SecurityUtils.getUsername(), driverIds);
        return toAjax(driverAppService.deleteDriverByIds(driverIds));
    }

}
