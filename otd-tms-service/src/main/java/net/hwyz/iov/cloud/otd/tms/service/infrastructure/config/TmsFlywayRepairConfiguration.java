package net.hwyz.iov.cloud.otd.tms.service.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 启动策略（OTD-TMS-DSN-CR-001）
 * <p>
 * TMS 首次引入 Flyway：环境库可能残留历史失败记录（例如迁移脚本修正前的一次失败），
 * 此时直接 migrate 会被 validate 阻断且无法自愈。开启本策略后启动先 repair（清理失败记录、
 * 对齐已执行脚本校验和）再 migrate。
 * <p>
 * 仅在 <b>首次引入/测试环境</b> 打开（{@code tms.flyway.repair-on-startup=true}）；
 * 生产稳定后应关闭，改为人工评估后执行 repair。
 *
 * @author hwyz_leo
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "tms.flyway.repair-on-startup", havingValue = "true")
public class TmsFlywayRepairConfiguration {

    /**
     * 启动时先 repair 再 migrate
     *
     * @return Flyway 迁移策略
     */
    @Bean
    public FlywayMigrationStrategy repairThenMigrate() {
        return flyway -> {
            log.warn("Flyway 启动策略：先 repair 再 migrate（tms.flyway.repair-on-startup=true）");
            flyway.repair();
            flyway.migrate();
        };
    }
}
