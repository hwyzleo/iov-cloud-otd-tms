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
import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeMpt;
import net.hwyz.iov.cloud.otd.tms.api.feign.mpt.LogisticsNodeMptApi;
import net.hwyz.iov.cloud.otd.tms.service.application.service.LogisticsNodeAppService;
import net.hwyz.iov.cloud.otd.tms.service.facade.assembler.LogisticsNodeMptAssembler;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流据点相关管理接口实现类
 *
 * @author hwyz_leo
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mpt/logisticsNode")
public class LogisticsNodeMptController extends BaseController implements LogisticsNodeMptApi {

    private final LogisticsNodeAppService logisticsNodeAppService;

    /**
     * 分页查询物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 物流据点信息列表
     */
    @RequiresPermissions("completeVehicle:transport:logisticsNode:list")
    @Override
    @GetMapping(value = "/list")
    public TableDataInfo list(LogisticsNodeMpt logisticsNode) {
        logger.info("管理后台用户[{}]分页查询物流据点信息", SecurityUtils.getUsername());
        startPage();
        List<LogisticsNodePo> logisticsNodePoList = logisticsNodeAppService.search(logisticsNode.getCode(), logisticsNode.getName(),
                getBeginTime(logisticsNode), getEndTime(logisticsNode));
        List<LogisticsNodeMpt> logisticsNodeMptList = LogisticsNodeMptAssembler.INSTANCE.fromPoList(logisticsNodePoList);
        return getDataTable(logisticsNodePoList, logisticsNodeMptList);
    }

    /**
     * 导出物流据点信息
     *
     * @param response      响应
     * @param logisticsNode 物流据点信息
     */
    @Log(title = "物流据点信息管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("completeVehicle:transport:logisticsNode:export")
    @Override
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsNodeMpt logisticsNode) {
        logger.info("管理后台用户[{}]导出物流据点类型", SecurityUtils.getUsername());
    }

    /**
     * 根据物流据点ID获取物流据点信息
     *
     * @param logisticsNodeId 物流据点ID
     * @return 物流据点信息
     */
    @RequiresPermissions("completeVehicle:transport:logisticsNode:query")
    @Override
    @GetMapping(value = "/{logisticsNodeId}")
    public AjaxResult getInfo(@PathVariable Long logisticsNodeId) {
        logger.info("管理后台用户[{}]根据物流据点ID[{}]获取物流据点信息", SecurityUtils.getUsername(), logisticsNodeId);
        LogisticsNodePo logisticsNodePo = logisticsNodeAppService.getLogisticsNodeById(logisticsNodeId);
        return success(LogisticsNodeMptAssembler.INSTANCE.fromPo(logisticsNodePo));
    }

    /**
     * 新增物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 结果
     */
    @Log(title = "物流据点信息管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("completeVehicle:transport:logisticsNode:add")
    @Override
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LogisticsNodeMpt logisticsNode) {
        logger.info("管理后台用户[{}]新增物流据点类型[{}]", SecurityUtils.getUsername(), logisticsNode.getCode());
        if (!logisticsNodeAppService.checkCodeUnique(logisticsNode.getId(), logisticsNode.getCode())) {
            return error("新增物流据点'" + logisticsNode.getCode() + "'失败，物流据点代码已存在");
        }
        LogisticsNodePo logisticsNodePo = LogisticsNodeMptAssembler.INSTANCE.toPo(logisticsNode);
        logisticsNodePo.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(logisticsNodeAppService.createLogisticsNode(logisticsNodePo));
    }

    /**
     * 修改保存物流据点信息
     *
     * @param logisticsNode 物流据点信息
     * @return 结果
     */
    @Log(title = "物流据点信息管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("completeVehicle:transport:logisticsNode:edit")
    @Override
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LogisticsNodeMpt logisticsNode) {
        logger.info("管理后台用户[{}]修改保存物流据点信息[{}]", SecurityUtils.getUsername(), logisticsNode.getCode());
        if (!logisticsNodeAppService.checkCodeUnique(logisticsNode.getId(), logisticsNode.getCode())) {
            return error("修改保存物流据点'" + logisticsNode.getCode() + "'失败，物流据点代码已存在");
        }
        LogisticsNodePo logisticsNodePo = LogisticsNodeMptAssembler.INSTANCE.toPo(logisticsNode);
        logisticsNodePo.setModifyBy(SecurityUtils.getUserId().toString());
        return toAjax(logisticsNodeAppService.modifyLogisticsNode(logisticsNodePo));
    }

    /**
     * 删除物流据点信息
     *
     * @param logisticsNodeIds 物流据点ID数组
     * @return 结果
     */
    @Log(title = "物流据点信息管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("completeVehicle:transport:logisticsNode:remove")
    @Override
    @DeleteMapping("/{logisticsNodeIds}")
    public AjaxResult remove(@PathVariable Long[] logisticsNodeIds) {
        logger.info("管理后台用户[{}]删除物流据点信息[{}]", SecurityUtils.getUsername(), logisticsNodeIds);
        return toAjax(logisticsNodeAppService.deleteLogisticsNodeByIds(logisticsNodeIds));
    }

}
