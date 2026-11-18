package net.hwyz.iov.cloud.otd.tms.service.facade.mpt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.audit.annotation.Log;
import net.hwyz.iov.cloud.framework.audit.enums.BusinessType;
import net.hwyz.iov.cloud.framework.web.controller.BaseController;
import net.hwyz.iov.cloud.framework.web.util.PageUtil;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;
import net.hwyz.iov.cloud.framework.security.annotation.RequiresPermissions;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import net.hwyz.iov.cloud.otd.tms.api.contract.LoadingVehicleMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.LoadingVehicleMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.LoadingVehicleAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.LoadingVehicleMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LoadingVehiclePo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装载车相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/loadingVehicle")
public class LoadingVehicleMptController extends BaseController implements LoadingVehicleMptApi {

    private final LoadingVehicleAppService loadingVehicleAppService;

    /**
     * 分页查询装载车
     *
     * @param loadVehicle 装载车
     * @return 装载车列表
     */
    @RequiresPermissions("completeVehicle:transport:loadVehicle:list")
    @Override
    @GetMapping(value = "/list")
    public ApiResponse<PageResult<LoadingVehicleMpt>> list(LoadingVehicleMpt loadVehicle) {
        log.info("管理后台用户[{}]分页查询装载车", SecurityUtils.getUsername());
        startPage();
        List<LoadingVehiclePo> loadVehiclePoList = loadingVehicleAppService.search(loadVehicle.getCarrierCode(), loadVehicle.getLicensePlate(),
                loadVehicle.getVehicleRegistrationNumber(), getBeginTime(loadVehicle), getEndTime(loadVehicle));
        return ApiResponse.ok(getPageResult(PageUtil.convert(loadVehiclePoList, LoadingVehicleMptAssembler.INSTANCE::fromPo)));
    }

    /**
     * 查询所有装载车（下拉选项）
     *
     * @return 装载车列表
     */
    @RequiresPermissions("completeVehicle:transport:loadVehicle:list")
    @Override
    @GetMapping(value = "/listAll")
    public List<LoadingVehicleMpt> listAll() {
        log.info("管理后台用户[{}]查询所有装载车", SecurityUtils.getUsername());
        List<LoadingVehiclePo> poList = loadingVehicleAppService.search(null, null, null, null, null);
        return LoadingVehicleMptAssembler.INSTANCE.fromPoList(poList);
    }

    /**
     * 导出装载车
     *
     * @param response    响应
     * @param loadVehicle 装载车
     */
    @Log(title = "装载车管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:loadVehicle:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, LoadingVehicleMpt loadVehicle) {
        log.info("管理后台用户[{}]导出装载车", SecurityUtils.getUsername());
    }

    /**
     * 根据装载车ID获取装载车
     *
     * @param loadVehicleId 装载车ID
     * @return 装载车
     */
    @RequiresPermissions("completeVehicle:transport:loadVehicle:query")
    @Override
    @GetMapping(value = "/{loadVehicleId}")
    public AjaxResult getInfo(@PathVariable Long loadVehicleId) {
        log.info("管理后台用户[{}]根据驾驶员ID[{}]获取装载车", SecurityUtils.getUsername(), loadVehicleId);
        LoadingVehiclePo loadingVehiclePo = loadingVehicleAppService.getLoadingVehicleById(loadVehicleId);
        return success(LoadingVehicleMptAssembler.INSTANCE.fromPo(loadingVehiclePo));
    }

    /**
     * 新增装载车
     *
     * @param loadVehicle 装载车
     * @return 结果
     */
    @Log(title = "装载车管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:loadVehicle:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LoadingVehicleMpt loadVehicle) {
        log.info("管理后台用户[{}]新增装载车[{}]", SecurityUtils.getUsername(), loadVehicle.getVehicleRegistrationNumber());
        if (!loadingVehicleAppService.checkVehicleRegistrationNumberUnique(loadVehicle.getId(), loadVehicle.getVehicleRegistrationNumber())) {
            return error("新增装载车'" + loadVehicle.getVehicleRegistrationNumber() + "'失败，装载车行驶证号已存在");
        }
        LoadingVehiclePo loadingVehiclePo = LoadingVehicleMptAssembler.INSTANCE.toPo(loadVehicle);
        loadingVehiclePo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(loadingVehicleAppService.createLoadingVehicle(loadingVehiclePo));
    }

    /**
     * 修改保存装载车
     *
     * @param loadVehicle 装载车
     * @return 结果
     */
    @Log(title = "装载车管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:loadVehicle:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LoadingVehicleMpt loadVehicle) {
        log.info("管理后台用户[{}]修改保存装载车[{}]", SecurityUtils.getUsername(), loadVehicle.getVehicleRegistrationNumber());
        if (!loadingVehicleAppService.checkVehicleRegistrationNumberUnique(loadVehicle.getId(), loadVehicle.getVehicleRegistrationNumber())) {
            return error("修改保存装载车'" + loadVehicle.getVehicleRegistrationNumber() + "'失败，装载车行驶证号已存在");
        }
        LoadingVehiclePo loadingVehiclePo = LoadingVehicleMptAssembler.INSTANCE.toPo(loadVehicle);
        loadingVehiclePo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(loadingVehicleAppService.modifyLoadingVehicle(loadingVehiclePo));
    }

    /**
     * 删除装载车
     *
     * @param loadVehicleIds 装载车ID数组
     * @return 结果
     */
    @Log(title = "装载车管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:driver:remove")
    @Override
    @DeleteMapping("/{loadVehicleIds}")
    public AjaxResult remove(@PathVariable Long[] loadVehicleIds) {
        log.info("管理后台用户[{}]删除装载车[{}]", SecurityUtils.getUsername(), loadVehicleIds);
        return toAjax(loadingVehicleAppService.deleteLoadingVehicleByIds(loadVehicleIds));
    }

}
