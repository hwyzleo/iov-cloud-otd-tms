package net.hwyz.iov.cloud.otd.tms.service.infrastructure.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.otd.tms.service.application.port.VsoOrderReportPort;
import net.hwyz.iov.cloud.otd.tms.service.application.service.TransportOrderAppService;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 待发运订单补偿任务（OTD-TMS-DSN-CR-001 §1.5）
 * <p>
 * 定时拉取 VSO 470 订单，为缺失运单的订单补建运单；单批有界、按页推进，已建单跳过。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tms.compensation.enabled", havingValue = "true", matchIfMissing = true)
public class TransportOrderCompensationScheduler {

    private final VsoOrderReportPort vsoOrderReportPort;
    private final TransportOrderAppService transportOrderAppService;
    private final TmsProperties properties;

    /**
     * 定时补偿
     */
    @Scheduled(fixedDelayString = "${tms.compensation.scan-interval-ms:300000}")
    public void compensate() {
        int pageSize = properties.getCompensation().getPageSize();
        int pageNum = 1;
        int created = 0;
        while (true) {
            List<VsoOrderReportPort.VsoTransportAppliedOrder> orders =
                    vsoOrderReportPort.findTransportApplied(null, pageNum, pageSize);
            if (orders.isEmpty()) {
                break;
            }
            for (VsoOrderReportPort.VsoTransportAppliedOrder order : orders) {
                try {
                    if (transportOrderAppService.compensate(order)) {
                        created++;
                    }
                } catch (Exception e) {
                    log.error("待发运补偿失败: orderNo={}, {}", order.orderNo(), e.getMessage(), e);
                }
            }
            if (orders.size() < pageSize) {
                break;
            }
            pageNum++;
        }
        if (created > 0) {
            log.info("待发运补偿完成，新建运单 {} 条", created);
        }
    }
}
