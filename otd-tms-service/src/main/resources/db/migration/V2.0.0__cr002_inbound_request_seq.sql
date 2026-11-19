-- OTD-TMS：交车触发交付库到货请求（OTD-TMS-DSN-CR-002）
-- 新增 tb_transport_order.inbound_request_seq：交付库到货请求批次序号（用于 requestId 与 Outbox 聚合版本）
-- 幂等：information_schema 守卫，可就地重跑。
SET @ddl := IF((SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tb_transport_order'
                  AND COLUMN_NAME = 'inbound_request_seq') = 0,
               'ALTER TABLE `tb_transport_order` ADD COLUMN `inbound_request_seq` INT NOT NULL DEFAULT 0 COMMENT ''交付库到货请求批次序号'' AFTER `last_reported_vso_state`',
               'DO 0');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
