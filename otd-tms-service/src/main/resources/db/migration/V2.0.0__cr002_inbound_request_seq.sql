-- OTD-TMS：交车触发交付库到货请求（OTD-TMS-DSN-CR-002）
-- 新增 tb_transport_order.inbound_request_seq：交付库到货请求批次序号（用于 requestId 与 Outbox 聚合版本）
-- 说明：不使用 PREPARE/动态 SQL（容器内 Druid WallFilter 会拦截），由 Flyway 版本化保证只执行一次。
ALTER TABLE `tb_transport_order`
    ADD COLUMN `inbound_request_seq` INT NOT NULL DEFAULT 0 COMMENT '交付库到货请求批次序号';
