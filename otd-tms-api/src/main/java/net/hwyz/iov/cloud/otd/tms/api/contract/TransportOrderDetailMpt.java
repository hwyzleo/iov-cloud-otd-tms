package net.hwyz.iov.cloud.otd.tms.api.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理后台运输单 VIN 明细（OTD-TMS-DSN-CR-001）
 * <p>
 * 状态快照来自 WMS 出库事实：RESERVED（已预留）/ LOADED（已装车）/ OUTBOUND（已出库）/ RELEASED（已释放）。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportOrderDetailMpt {

    /**
     * 运输单号
     */
    private String orderNum;

    /**
     * 车辆 VIN
     */
    private String vin;

    /**
     * WMS 明细状态快照
     */
    private String state;

    /**
     * WMS 明细状态名称
     */
    private String stateName;

    /**
     * 出库单号
     */
    private String outboundOrderNum;

    /**
     * 装载行号
     */
    private Integer loadingLine;
}
