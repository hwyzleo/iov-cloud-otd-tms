package net.hwyz.iov.cloud.otd.tms.service.infrastructure.config;

import net.hwyz.iov.cloud.framework.web.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

/**
 * 事件时区装配（OTD-TMS-DSN-CR-001 INV-006）
 * <p>
 * 与 JSON 契约共用同一个时区来源 {@code iov.web.jackson.zone-id}：事件时间必须显式带偏移，
 * 禁止回退 JVM 默认时区；未配置时 fail-closed。
 *
 * @author hwyz_leo
 */
@Configuration
public class TmsTimeZoneConfiguration {

    @Bean("eventZone")
    public ZoneId eventZone(JacksonProperties jacksonProperties) {
        ZoneId zoneId = jacksonProperties.getZoneId();
        if (zoneId == null) {
            throw new IllegalStateException(
                    "iov.web.jackson.zone-id 未配置：事件 ISO-8601 输出需要显式时区（全 UTC 栈应为 UTC）");
        }
        return zoneId;
    }
}
