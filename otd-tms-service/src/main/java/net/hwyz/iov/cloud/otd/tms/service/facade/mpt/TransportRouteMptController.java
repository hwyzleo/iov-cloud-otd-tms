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
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportRouteMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.TransportRouteMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportRouteAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.TransportRouteMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运输线路相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/transportRoute")
public class TransportRouteMptController extends BaseController implements TransportRouteMptApi {

    private final TransportRouteAppService transportRouteAppService;

    /**
     * 分页查询运输线路
     *
     * @param transportRoute 运输线路
     * @return 运输线路列表
     */
    @RequiresPermissions("completeVehicle:transport:transportRoute:list")
    @Override
    @GetMapping(value = "/list")
    public ApiResponse<PageResult<TransportRouteMpt>> list(TransportRouteMpt transportRoute) {
        log.info("管理后台用户[{}]分页查询运输线路", SecurityUtils.getUsername());
        startPage();
        List<TransportRoutePo> transportRoutePoList = transportRouteAppService.search(transportRoute.getCarrierCode(),
                transportRoute.getCode(), transportRoute.getName(), getBeginTime(transportRoute), getEndTime(transportRoute));
        return ApiResponse.ok(getPageResult(PageUtil.convert(transportRoutePoList, TransportRouteMptAssembler.INSTANCE::fromPo)));
    }

    /**
     * 查询所有运输线路（下拉选项）
     *
     * @return 运输线路列表
     */
    @RequiresPermissions("completeVehicle:transport:transportRoute:list")
    @Override
    @GetMapping(value = "/listAll")
    public List<TransportRouteMpt> listAll() {
        log.info("管理后台用户[{}]查询所有运输线路", SecurityUtils.getUsername());
        List<TransportRoutePo> poList = transportRouteAppService.search(null, null, null, null, null);
        return TransportRouteMptAssembler.INSTANCE.fromPoList(poList);
    }

    /**
     * 导出运输线路
     *
     * @param response       响应
     * @param transportRoute 运输线路
     */
    @Log(title = "运输线路管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:transportRoute:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, TransportRouteMpt transportRoute) {
        log.info("管理后台用户[{}]导出运输线路", SecurityUtils.getUsername());
    }

    /**
     * 根据运输线路ID获取运输线路
     *
     * @param transportRouteId 运输线路ID
     * @return 运输线路
     */
    @RequiresPermissions("completeVehicle:transport:transportRoute:query")
    @Override
    @GetMapping(value = "/{transportRouteId}")
    public AjaxResult getInfo(@PathVariable Long transportRouteId) {
        log.info("管理后台用户[{}]根据运输线路ID[{}]获取运输线路", SecurityUtils.getUsername(), transportRouteId);
        TransportRoutePo transportRoutePo = transportRouteAppService.getTransportRouteById(transportRouteId);
        return success(TransportRouteMptAssembler.INSTANCE.fromPo(transportRoutePo));
    }

    /**
     * 新增运输线路
     *
     * @param transportRoute 运输线路
     * @return 结果
     */
    @Log(title = "运输线路管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:transportRoute:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody TransportRouteMpt transportRoute) {
        log.info("管理后台用户[{}]新增运输线路[{}]", SecurityUtils.getUsername(), transportRoute.getCode());
        if (!transportRouteAppService.checkCodeUnique(transportRoute.getId(), transportRoute.getCode())) {
            return error("新增运输线路'" + transportRoute.getCode() + "'失败，运输线路代码已存在");
        }
        TransportRoutePo transportRoutePo = TransportRouteMptAssembler.INSTANCE.toPo(transportRoute);
        transportRoutePo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(transportRouteAppService.createTransportRoute(transportRoutePo));
    }

    /**
     * 修改保存运输线路
     *
     * @param transportRoute 运输线路
     * @return 结果
     */
    @Log(title = "运输线路管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportRoute:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody TransportRouteMpt transportRoute) {
        log.info("管理后台用户[{}]修改保存运输线路[{}]", SecurityUtils.getUsername(), transportRoute.getCode());
        if (!transportRouteAppService.checkCodeUnique(transportRoute.getId(), transportRoute.getCode())) {
            return error("修改保存运输线路'" + transportRoute.getCode() + "'失败，运输线路代码已存在");
        }
        TransportRoutePo transportRoutePo = TransportRouteMptAssembler.INSTANCE.toPo(transportRoute);
        transportRoutePo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(transportRouteAppService.modifyTransportType(transportRoutePo));
    }

    /**
     * 删除运输线路
     *
     * @param transportRouteIds 运输线路ID数组
     * @return 结果
     */
    @Log(title = "运输线路管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:transportRoute:remove")
    @Override
    @DeleteMapping("/{transportRouteIds}")
    public AjaxResult remove(@PathVariable Long[] transportRouteIds) {
        log.info("管理后台用户[{}]删除运输线路[{}]", SecurityUtils.getUsername(), transportRouteIds);
        return toAjax(transportRouteAppService.deleteTransportRouteByIds(transportRouteIds));
    }

}
