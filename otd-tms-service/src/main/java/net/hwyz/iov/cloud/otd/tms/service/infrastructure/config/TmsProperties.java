package net.hwyz.iov.cloud.otd.tms.service.infrastructure.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * TMS 类型化配置（Topic 注册表、消费组、中继与补偿参数，OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "tms")
public class TmsProperties {

    /**
     * Topic 注册表
     */
    private Topics topics = new Topics();

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 出站消息
     */
    private Outbox outbox = new Outbox();

    /**
     * 补偿任务
     */
    private Compensation compensation = new Compensation();

    /**
     * 来源系统标识
     */
    @NotBlank
    private String sourceSystem = "TMS";

    @Data
    public static class Topics {
        /**
         * VSO 发运申请事实
         */
        @NotBlank
        private String vsoTransportApplied = "vso.transport.applied";
        /**
         * WMS 出库指令
         */
        @NotBlank
        private String wmsOutboundRequested = "wms.vehicle-outbound.requested";
        /**
         * WMS 出库取消指令
         */
        @NotBlank
        private String wmsOutboundCancellationRequested = "wms.vehicle-outbound.cancellation-requested";
        /**
         * WMS 出库事实
         */
        @NotBlank
        private String wmsOutboundChanged = "wms.vehicle-outbound.changed";
        /**
         * WMS 出库命令拒绝事实
         */
        @NotBlank
        private String wmsOutboundCommandRejected = "wms.vehicle-outbound.command-rejected";
        /**
         * VSO 发运申请死信
         */
        @NotBlank
        private String vsoTransportAppliedDlq = "vso.transport.applied.tms.dlq";
        /**
         * WMS 出库事实死信
         */
        @NotBlank
        private String wmsOutboundChangedDlq = "wms.vehicle-outbound.changed.tms.dlq";
        /**
         * WMS 到货请求（TMS 生产）
         */
        @NotBlank
        private String wmsInboundRequested = "wms.vehicle-inbound.requested";
    }

    @Data
    public static class Consumer {
        /**
         * 是否自动启动消费者
         */
        private boolean autoStartup = true;
        /**
         * 发运申请消费组
         */
        @NotBlank
        private String transportAppliedGroup = "otd-tms-transport-applied";
        /**
         * 出库事实消费组
         */
        @NotBlank
        private String outboundFactGroup = "otd-tms-outbound-fact";
    }

    @Data
    public static class Outbox {
        /**
         * 聚合类型
         */
        @NotBlank
        private String aggregateType = "TRANSPORT_ORDER";
        /**
         * 中继参数
         */
        private Relay relay = new Relay();
    }

    @Data
    public static class Relay {
        private boolean enabled = true;
        @Min(1000)
        private long scanIntervalMs = 10000;
        @Min(1)
        private int batchSize = 100;
        @Min(1)
        private int maxRetry = 10;
        @Min(1000)
        private long baseBackoffMs = 5000;
        @Min(1000)
        private long maxBackoffMs = 300000;
    }

    @Data
    public static class Compensation {
        private boolean enabled = true;
        @Min(10000)
        private long scanIntervalMs = 300000;
        @Min(1)
        private int pageSize = 100;
    }
}
