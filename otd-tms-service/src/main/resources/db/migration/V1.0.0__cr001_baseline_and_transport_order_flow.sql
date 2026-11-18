-- OTD-TMS-DSN-CR-001：运单主流程数据模型
-- 说明：
-- 1) 本脚本对「既有库」（doc/sql/init.sql 初始化）与「空库」均幂等可用；
-- 2) TMS 尚未产生运单业务数据，运单两表按新结构重建；
-- 3) tb_logistics_node 仅补列（既有主数据不重建），用 information_schema 判定避免重复添加。

-- 入站消息去重（Inbox）
CREATE TABLE IF NOT EXISTS `tb_tms_inbox`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `consumer_name`   VARCHAR(100) NOT NULL COMMENT '消费者名称',
    `topic`           VARCHAR(255) NOT NULL COMMENT '来源 Topic',
    `event_id`        VARCHAR(64)  NOT NULL COMMENT '事件唯一 ID',
    `event_type`      VARCHAR(128) NOT NULL COMMENT '事件类型',
    `payload_digest`  VARCHAR(64)  NOT NULL COMMENT '规范化载荷摘要',
    `process_state`   VARCHAR(32)  NOT NULL COMMENT '处理状态：PROCESSED/BUSINESS_REJECTED/DEAD_LETTERED',
    `business_key`    VARCHAR(255)          DEFAULT NULL COMMENT '业务幂等键',
    `result_event_id` VARCHAR(64)           DEFAULT NULL COMMENT '对应结果事件 ID',
    `error_code`      VARCHAR(32)           DEFAULT NULL COMMENT '错误码',
    `error_message`   VARCHAR(2000)         DEFAULT NULL COMMENT '脱敏错误信息',
    `received_time`   DATETIME     NOT NULL COMMENT '接收时间',
    `processed_time`  DATETIME              DEFAULT NULL COMMENT '处理完成时间',
    `description`     VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`       BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`     INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`       TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tms_inbox_consumer_event` (`consumer_name`, `event_id`),
    KEY `idx_tms_inbox_topic_time` (`topic`, `received_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='TMS 入站消息去重';

-- 出站消息发件箱（Outbox）
CREATE TABLE IF NOT EXISTS `tb_tms_outbox`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `event_id`          VARCHAR(64)  NOT NULL COMMENT '事件唯一 ID',
    `event_type`        VARCHAR(128) NOT NULL COMMENT '事件类型',
    `aggregate_type`    VARCHAR(64)  NOT NULL COMMENT '聚合类型',
    `aggregate_id`      VARCHAR(255) NOT NULL COMMENT '聚合 ID',
    `aggregate_version` BIGINT       NOT NULL COMMENT '聚合版本',
    `topic`             VARCHAR(255) NOT NULL COMMENT '注册的目标 Topic',
    `message_key`       VARCHAR(255) NOT NULL COMMENT 'Kafka Key',
    `payload`           TEXT         NOT NULL COMMENT '不可变 JSON 载荷',
    `publish_state`     VARCHAR(32)  NOT NULL COMMENT '发布状态：PENDING/FAILED_RETRYABLE/PUBLISHED/DEAD',
    `retry_count`       INT          NOT NULL DEFAULT 0 COMMENT '已重试次数',
    `next_retry_time`   DATETIME              DEFAULT NULL COMMENT '下次重试时间',
    `published_at`      DATETIME              DEFAULT NULL COMMENT 'Broker 确认时间',
    `last_error`        VARCHAR(2000)         DEFAULT NULL COMMENT '脱敏的最后错误',
    `description`       VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`         BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`       INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`         TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tms_outbox_event_id` (`event_id`),
    KEY `idx_tms_outbox_publish_scan` (`publish_state`, `next_retry_time`),
    KEY `idx_tms_outbox_aggregate` (`aggregate_type`, `aggregate_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='TMS 出站消息发件箱';

-- 运输单（重建：新增来源/仓库/回写状态/拒绝原因）
DROP TABLE IF EXISTS `tb_transport_order_detail`;
DROP TABLE IF EXISTS `tb_transport_order`;
CREATE TABLE `tb_transport_order`
(
    `id`                           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_num`                    VARCHAR(50) NOT NULL COMMENT '运输单号',
    `source_system`                VARCHAR(32)          DEFAULT NULL COMMENT '来源系统（VSO）',
    `source_order_no`              VARCHAR(64)          DEFAULT NULL COMMENT '来源订单号（VSO 订单号）',
    `warehouse_code`               VARCHAR(64)          DEFAULT NULL COMMENT '出库仓库代码',
    `carrier_code`                 VARCHAR(50)          DEFAULT NULL COMMENT '承运商代码',
    `state`                        SMALLINT    NOT NULL COMMENT '状态：1-未发运，2-运输中，3-部分交车，4-交车完成，5-部分接车，6-接车完成，7-手工结束，9-已取消',
    `transport_route_code`         VARCHAR(50)          DEFAULT NULL COMMENT '运输线路代码',
    `dispatch_logistics_node_code` VARCHAR(50)          DEFAULT NULL COMMENT '发车据点代码',
    `estimated_loading_time`       DATETIME             DEFAULT NULL COMMENT '预计装车时间',
    `estimated_arrival_time`       DATETIME             DEFAULT NULL COMMENT '预计到达时间',
    `vehicle_dispatch_time`        DATETIME             DEFAULT NULL COMMENT '发车时间',
    `handover_logistics_node_code` VARCHAR(50)          DEFAULT NULL COMMENT '接车据点代码',
    `delivery_time`                DATETIME             DEFAULT NULL COMMENT '交车时间',
    `receipt_time`                 DATETIME             DEFAULT NULL COMMENT '接车时间',
    `license_plate`                VARCHAR(100)         DEFAULT NULL COMMENT '运输车辆',
    `id_number`                    VARCHAR(50)          DEFAULT NULL COMMENT '驾驶员身份证号',
    `dispatcher`                   VARCHAR(100)         DEFAULT NULL COMMENT '调度人员',
    `dispatch_time`                DATETIME             DEFAULT NULL COMMENT '调度时间',
    `confirm`                      TINYINT              DEFAULT NULL COMMENT '是否确认',
    `reject_reason`                VARCHAR(255)         DEFAULT NULL COMMENT '出库拒绝原因码',
    `dispatch_version`             INT         NOT NULL DEFAULT 0 COMMENT '调度版本（出库指令幂等依据）',
    `last_reported_vso_state`      SMALLINT             DEFAULT NULL COMMENT '已回写 VSO 的订单状态值',
    `description`                  VARCHAR(255)         DEFAULT NULL COMMENT '备注',
    `create_time`                  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                    BIGINT               DEFAULT NULL COMMENT '创建者',
    `modify_time`                  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`                    BIGINT               DEFAULT NULL COMMENT '修改者',
    `row_version`                  INT                  DEFAULT NULL COMMENT '记录版本',
    `row_valid`                    TINYINT              DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transport_order_num` (`order_num`),
    UNIQUE KEY `uk_transport_order_source` (`source_system`, `source_order_no`, `row_valid`),
    KEY `idx_transport_order_state` (`state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运输订单';

CREATE TABLE `tb_transport_order_detail`
(
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_num`          VARCHAR(50) NOT NULL COMMENT '运输单号',
    `vin`                VARCHAR(20) NOT NULL COMMENT '车架号',
    `state`              VARCHAR(32)          DEFAULT NULL COMMENT 'WMS 明细状态快照：RESERVED/LOADED/OUTBOUND/RELEASED',
    `outbound_order_num` VARCHAR(64)          DEFAULT NULL COMMENT 'WMS 出库单号',
    `loading_line`       SMALLINT             DEFAULT NULL COMMENT '装载道',
    `delivery_time`      DATETIME             DEFAULT NULL COMMENT '交车时间',
    `receipt_time`       DATETIME             DEFAULT NULL COMMENT '接车时间',
    `lock`               TINYINT              DEFAULT NULL COMMENT '是否锁定',
    `description`        VARCHAR(255)         DEFAULT NULL COMMENT '备注',
    `create_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`          BIGINT               DEFAULT NULL COMMENT '创建者',
    `modify_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`          BIGINT               DEFAULT NULL COMMENT '修改者',
    `row_version`        INT                  DEFAULT NULL COMMENT '记录版本',
    `row_valid`          TINYINT              DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    KEY `idx_transport_order_detail_num` (`order_num`),
    KEY `idx_transport_order_detail_vin` (`vin`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运输订单明细';

-- 据点 → WMS 仓库映射（仅补列，既有主数据保留）
-- 说明：Flyway 对同一数据库只执行一次本迁移，因此使用无条件 ALTER；
-- 前提是 tb_logistics_node 已由 doc/sql/init.sql 建立（TMS 主数据基线）。
ALTER TABLE tb_logistics_node
    ADD COLUMN warehouse_code VARCHAR(64) NULL COMMENT 'WMS 仓库代码' AFTER type_code;
