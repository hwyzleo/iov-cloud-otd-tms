package net.hwyz.iov.cloud.otd.tms.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TMS 错误码（203xxx 段，OTD-TMS-DSN-CR-001 §1.6）
 *
 * @author hwyz_leo
 */
@Getter
@AllArgsConstructor
public enum TmsErrorCode {

    /**
     * 运单不存在
     */
    ORDER_NOT_EXIST("203001", "运单不存在"),
    /**
     * 运单状态不允许该操作
     */
    STATE_NOT_ALLOWED("203002", "运单状态不允许该操作"),
    /**
     * 出库指令被 WMS 拒绝
     */
    OUTBOUND_REJECTED("203003", "出库指令被 WMS 拒绝"),
    /**
     * 运单已存在（来源订单号重复）
     */
    ORDER_EXISTS("203004", "运单已存在"),
    /**
     * 上游 VSO 回写失败
     */
    VSO_REPORT_FAILED("203005", "上游订单状态回写失败"),
    /**
     * 事件信封或 schemaVersion 非法
     */
    MESSAGE_INVALID("203006", "事件信封或 schemaVersion 非法"),
    /**
     * 主数据不可用
     */
    MASTER_DATA_UNAVAILABLE("203007", "主数据不可用"),
    /**
     * VIN 已在其它有效运单中
     */
    VIN_ALREADY_IN_ORDER("203008", "VIN 已在其它有效运单中"),
    /**
     * VIN 不属于该运单
     */
    VIN_NOT_IN_ORDER("203009", "VIN 不属于该运单"),
    /**
     * 接车据点未配置仓库
     */
    HANDOVER_WAREHOUSE_MISSING("203010", "接车据点未配置仓库");

    private final String code;
    private final String message;
}
