package net.hwyz.iov.cloud.otd.tms.service.application.port;

import java.util.List;

/**
 * 订单状态回写端口（TMS → VSO，OTD-TMS-DSN-CR-001 §1.5）
 * <p>
 * 订单状态以 VSO 为唯一事实：TMS 只回写「准备运输 500」「运输中 550」「待交付 600」三个状态，
 * 并可按 470 拉取待发运订单做补偿。
 *
 * @author hwyz_leo
 */
public interface VsoOrderReportPort {

    /**
     * 回写准备运输（470 → 500）
     *
     * @param orderNo 订单号
     */
    void prepareTransport(String orderNo);

    /**
     * 回写运输中（500 → 550）
     *
     * @param orderNo 订单号
     */
    void transporting(String orderNo);

    /**
     * 回写待交付（550 → 600）
     *
     * @param orderNo 订单号
     */
    void prepareDelivery(String orderNo);

    /**
     * 拉取待发运订单（补偿通道）
     *
     * @param deliveryRegionCode 交付区域（可空）
     * @param pageNum            页码
     * @param pageSize           每页条数
     * @return 待发运订单
     */
    List<VsoTransportAppliedOrder> findTransportApplied(String deliveryRegionCode, int pageNum, int pageSize);

    /**
     * VSO 待发运订单摘要
     *
     * @param orderNo          订单号
     * @param vin              交付 VIN
     * @param deliveryStoreCode 交付门店
     * @param deliveryRegionCode 交付区域
     */
    record VsoTransportAppliedOrder(String orderNo, String vin, String deliveryStoreCode,
                                    String deliveryRegionCode) {
    }
}
