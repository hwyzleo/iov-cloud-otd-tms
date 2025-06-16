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
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportTypeMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.TransportTypeMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportTypeAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.TransportTypeMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportTypePo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运输方式相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/transportType")
public class TransportTypeMptController extends BaseController implements TransportTypeMptApi {

    private final TransportTypeAppService transportTypeAppService;

    /**
     * 分页查询运输方式
     *
     * @param transportType 运输方式
     * @return 运输方式列表
     */
    @RequiresPermissions("completeVehicle:transport:transportType:list")
    @Override
    @GetMapping(value = "/list")
    public TableDataInfo list(TransportTypeMpt transportType) {
        logger.info("管理后台用户[{}]分页查询运输方式", SecurityUtils.getUsername());
        startPage();
        List<TransportTypePo> transportTypePoList = transportTypeAppService.search(transportType.getCode(),
                transportType.getName(), getBeginTime(transportType), getEndTime(transportType));
        List<TransportTypeMpt> transportTypeMptList = TransportTypeMptAssembler.INSTANCE.fromPoList(transportTypePoList);
        return getDataTable(transportTypePoList, transportTypeMptList);
    }

    /**
     * 查询所有运输方式
     *
     * @return 运输方式列表
     */
    @RequiresPermissions("completeVehicle:transport:transportType:list")
    @Override
    @GetMapping(value = "/listAll")
    public List<TransportTypeMpt> listAll() {
        logger.info("管理后台用户[{}]查询所有运输方式", SecurityUtils.getUsername());
        List<TransportTypePo> transportTypePoList = transportTypeAppService.search(null, null, null, null);
        return TransportTypeMptAssembler.INSTANCE.fromPoList(transportTypePoList);
    }

    /**
     * 导出运输方式
     *
     * @param response      响应
     * @param transportType 运输方式
     */
    @Log(title = "运输方式管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:transportType:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, TransportTypeMpt transportType) {
        logger.info("管理后台用户[{}]导出运输方式", SecurityUtils.getUsername());
    }

    /**
     * 根据运输方式ID获取运输方式
     *
     * @param transportTypeId 运输方式ID
     * @return 运输方式
     */
    @RequiresPermissions("completeVehicle:transport:transportType:query")
    @Override
    @GetMapping(value = "/{transportTypeId}")
    public AjaxResult getInfo(@PathVariable Long transportTypeId) {
        logger.info("管理后台用户[{}]根据运输方式ID[{}]获取运输方式", SecurityUtils.getUsername(), transportTypeId);
        TransportTypePo transportTypePo = transportTypeAppService.getTransportTypeById(transportTypeId);
        return success(TransportTypeMptAssembler.INSTANCE.fromPo(transportTypePo));
    }

    /**
     * 新增运输方式
     *
     * @param transportType 运输方式
     * @return 结果
     */
    @Log(title = "运输方式管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:transportType:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody TransportTypeMpt transportType) {
        logger.info("管理后台用户[{}]新增运输方式[{}]", SecurityUtils.getUsername(), transportType.getCode());
        if (!transportTypeAppService.checkCodeUnique(transportType.getId(), transportType.getCode())) {
            return error("新增运输方式'" + transportType.getCode() + "'失败，运输方式代码已存在");
        }
        TransportTypePo transportTypePo = TransportTypeMptAssembler.INSTANCE.toPo(transportType);
        transportTypePo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(transportTypeAppService.createTransportType(transportTypePo));
    }

    /**
     * 修改保存运输方式
     *
     * @param transportType 运输方式
     * @return 结果
     */
    @Log(title = "运输方式管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:transportType:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody TransportTypeMpt transportType) {
        logger.info("管理后台用户[{}]修改保存运输方式[{}]", SecurityUtils.getUsername(), transportType.getCode());
        if (!transportTypeAppService.checkCodeUnique(transportType.getId(), transportType.getCode())) {
            return error("修改保存运输方式'" + transportType.getCode() + "'失败，运输方式代码已存在");
        }
        TransportTypePo transportTypePo = TransportTypeMptAssembler.INSTANCE.toPo(transportType);
        transportTypePo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(transportTypeAppService.modifyTransportType(transportTypePo));
    }

    /**
     * 删除运输方式
     *
     * @param transportTypeIds 运输方式ID数组
     * @return 结果
     */
    @Log(title = "运输方式管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:transportType:remove")
    @Override
    @DeleteMapping("/{transportTypeIds}")
    public AjaxResult remove(@PathVariable Long[] transportTypeIds) {
        logger.info("管理后台用户[{}]删除运输方式[{}]", SecurityUtils.getUsername(), transportTypeIds);
        return toAjax(transportTypeAppService.deleteTransportTypeByIds(transportTypeIds));
    }

}
