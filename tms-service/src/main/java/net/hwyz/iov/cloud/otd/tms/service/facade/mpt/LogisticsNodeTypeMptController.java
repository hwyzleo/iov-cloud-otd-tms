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
import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeTypeMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.LogisticsNodeTypeMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.LogisticsNodeTypeAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.LogisticsNodeTypeMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流据点类型相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/logisticsNodeType")
public class LogisticsNodeTypeMptController extends BaseController implements LogisticsNodeTypeMptApi {

    private final LogisticsNodeTypeAppService logisticsNodeTypeAppService;

    /**
     * 分页查询物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 物流据点类型列表
     */
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:list")
    @Override
    @GetMapping(value = "/list")
    public TableDataInfo list(LogisticsNodeTypeMpt logisticsNodeType) {
        logger.info("管理后台用户[{}]分页查询物流据点类型", SecurityUtils.getUsername());
        startPage();
        List<LogisticsNodeTypePo> logisticsNodeTypePoList = logisticsNodeTypeAppService.search(logisticsNodeType.getCode(),
                logisticsNodeType.getName(), getBeginTime(logisticsNodeType), getEndTime(logisticsNodeType));
        List<LogisticsNodeTypeMpt> logisticsNodeTypeMptList = LogisticsNodeTypeMptAssembler.INSTANCE.fromPoList(logisticsNodeTypePoList);
        return getDataTable(logisticsNodeTypePoList, logisticsNodeTypeMptList);
    }

    /**
     * 导出物流据点类型
     *
     * @param response          响应
     * @param logisticsNodeType 物流据点类型
     */
    @Log(title = "物流据点类型管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsNodeTypeMpt logisticsNodeType) {
        logger.info("管理后台用户[{}]导出物流据点类型", SecurityUtils.getUsername());
    }

    /**
     * 根据物流据点类型ID获取物流据点类型
     *
     * @param logisticsNodeTypeId 物流据点类型ID
     * @return 物流据点类型
     */
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:query")
    @Override
    @GetMapping(value = "/{logisticsNodeTypeId}")
    public AjaxResult getInfo(@PathVariable Long logisticsNodeTypeId) {
        logger.info("管理后台用户[{}]根据物流据点类型ID[{}]获取物流据点类型", SecurityUtils.getUsername(), logisticsNodeTypeId);
        LogisticsNodeTypePo logisticsNodeTypePo = logisticsNodeTypeAppService.getLogisticsNodeTypeById(logisticsNodeTypeId);
        return success(LogisticsNodeTypeMptAssembler.INSTANCE.fromPo(logisticsNodeTypePo));
    }

    /**
     * 新增物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 结果
     */
    @Log(title = "物流据点类型管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LogisticsNodeTypeMpt logisticsNodeType) {
        logger.info("管理后台用户[{}]新增物流据点类型[{}]", SecurityUtils.getUsername(), logisticsNodeType.getCode());
        if (!logisticsNodeTypeAppService.checkCodeUnique(logisticsNodeType.getId(), logisticsNodeType.getCode())) {
            return error("新增物流据点类型'" + logisticsNodeType.getCode() + "'失败，物流据点类型代码已存在");
        }
        LogisticsNodeTypePo logisticsNodeTypePo = LogisticsNodeTypeMptAssembler.INSTANCE.toPo(logisticsNodeType);
        logisticsNodeTypePo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(logisticsNodeTypeAppService.createLogisticsNodeType(logisticsNodeTypePo));
    }

    /**
     * 修改保存物流据点类型
     *
     * @param logisticsNodeType 物流据点类型
     * @return 结果
     */
    @Log(title = "物流据点类型管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LogisticsNodeTypeMpt logisticsNodeType) {
        logger.info("管理后台用户[{}]修改保存物流据点类型[{}]", SecurityUtils.getUsername(), logisticsNodeType.getCode());
        if (!logisticsNodeTypeAppService.checkCodeUnique(logisticsNodeType.getId(), logisticsNodeType.getCode())) {
            return error("修改保存物流据点类型'" + logisticsNodeType.getCode() + "'失败，物流据点类型代码已存在");
        }
        LogisticsNodeTypePo logisticsNodeTypePo = LogisticsNodeTypeMptAssembler.INSTANCE.toPo(logisticsNodeType);
        logisticsNodeTypePo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(logisticsNodeTypeAppService.modifyLogisticsNodeType(logisticsNodeTypePo));
    }

    /**
     * 删除物流据点类型
     *
     * @param logisticsNodeTypeIds 物流据点类型ID数组
     * @return 结果
     */
    @Log(title = "物流据点类型管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:logisticsNodeType:remove")
    @Override
    @DeleteMapping("/{logisticsNodeTypeIds}")
    public AjaxResult remove(@PathVariable Long[] logisticsNodeTypeIds) {
        logger.info("管理后台用户[{}]删除物流据点类型[{}]", SecurityUtils.getUsername(), logisticsNodeTypeIds);
        return toAjax(logisticsNodeTypeAppService.deleteLogisticsNodeTypeByIds(logisticsNodeTypeIds));
    }

}
