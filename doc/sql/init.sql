DROP TABLE IF EXISTS `db_tms`.`tb_logistics_node_type`;
CREATE TABLE `db_tms`.`tb_logistics_node_type`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(50)  NOT NULL COMMENT '据点类型代码',
    `name`        VARCHAR(255) NOT NULL COMMENT '据点类型名称',
    `enable`      TINYINT      NOT NULL COMMENT '是否启用',
    `sort`        INT          NOT NULL COMMENT '排序',
    `description` VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`   BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version` INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`   TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='物流据点类型';

DROP TABLE IF EXISTS `db_tms`.`tb_logistics_node`;
CREATE TABLE `db_tms`.`tb_logistics_node`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`           VARCHAR(50)  NOT NULL COMMENT '据点代码',
    `name`           VARCHAR(255) NOT NULL COMMENT '据点名称',
    `type_code`      VARCHAR(50)  NOT NULL COMMENT '据点类型代码',
    `province_code`  VARCHAR(20)           DEFAULT NULL COMMENT '省级行政区代码',
    `city_code`      VARCHAR(20)           DEFAULT NULL COMMENT '地区级行政区代码',
    `county_code`    VARCHAR(20)           DEFAULT NULL COMMENT '县级行政区代码',
    `address`        VARCHAR(255)          DEFAULT NULL COMMENT '据点地址',
    `contact_person` VARCHAR(100)          DEFAULT NULL COMMENT '联系人',
    `contact_number` VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    `enable`         TINYINT      NOT NULL COMMENT '是否启用',
    `sort`           INT          NOT NULL COMMENT '排序',
    `description`    VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`      BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`      BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`    INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`      TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`code`),
    KEY `idx_type_code` (`type_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='物流据点';

DROP TABLE IF EXISTS `db_tms`.`tb_transport_type`;
CREATE TABLE `db_tms`.`tb_transport_type`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(50)  NOT NULL COMMENT '运输方式代码',
    `name`        VARCHAR(255) NOT NULL COMMENT '运输方式名称',
    `enable`      TINYINT      NOT NULL COMMENT '是否启用',
    `sort`        INT          NOT NULL COMMENT '排序',
    `description` VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`   BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version` INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`   TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运输方式';

DROP TABLE IF EXISTS `db_tms`.`tb_carrier`;
CREATE TABLE `db_tms`.`tb_carrier`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`           VARCHAR(50)  NOT NULL COMMENT '承运商代码',
    `name`           VARCHAR(255) NOT NULL COMMENT '承运商名称',
    `address`        VARCHAR(255)          DEFAULT NULL COMMENT '公司地址',
    `contact_person` VARCHAR(100)          DEFAULT NULL COMMENT '联系人',
    `contact_number` VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    `enable`         TINYINT      NOT NULL COMMENT '是否启用',
    `sort`           INT          NOT NULL COMMENT '排序',
    `description`    VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`      BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`      BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`    INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`      TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='承运商';

DROP TABLE IF EXISTS `db_tms`.`tb_driver`;
CREATE TABLE `db_tms`.`tb_driver`
(
    `id`                     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `carrier_code`           VARCHAR(50)  NOT NULL COMMENT '承运商代码',
    `real_name`              VARCHAR(100) NOT NULL COMMENT '驾驶员姓名',
    `contact_number`         VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    `id_number`              VARCHAR(50)           DEFAULT NULL COMMENT '身份证号',
    `driving_license_number` VARCHAR(50)           DEFAULT NULL COMMENT '驾驶证号',
    `enable`                 TINYINT      NOT NULL COMMENT '是否启用',
    `sort`                   INT          NOT NULL COMMENT '排序',
    `description`            VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`              BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`              BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`            INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`              TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`id_number`),
    KEY `idx_carrier_code` (`carrier_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='驾驶员';

DROP TABLE IF EXISTS `db_tms`.`tb_loading_vehicle`;
CREATE TABLE `db_tms`.`tb_loading_vehicle`
(
    `id`                          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `carrier_code`                VARCHAR(50)  NOT NULL COMMENT '承运商代码',
    `license_plate`               VARCHAR(100) NOT NULL COMMENT '车牌',
    `vehicle_type`                VARCHAR(20)  NOT NULL COMMENT '车型：C6-6位车，C7-7位车，C8-8位车，RV-救援车',
    `max_loading_capacity`        SMALLINT              DEFAULT NULL COMMENT '最大装载数',
    `vehicle_registration_number` VARCHAR(50)           DEFAULT NULL COMMENT '行驶证号',
    `enable`                      TINYINT      NOT NULL COMMENT '是否启用',
    `sort`                        INT          NOT NULL COMMENT '排序',
    `description`                 VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                   BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`                   BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`                 INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`                   TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`license_plate`),
    KEY `idx_carrier_code` (`carrier_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='装载车';

DROP TABLE IF EXISTS `db_tms`.`tb_transport_route`;
CREATE TABLE `db_tms`.`tb_transport_route`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `carrier_code` VARCHAR(50)  NOT NULL COMMENT '承运商代码',
    `code`         VARCHAR(50)  NOT NULL COMMENT '线路代码',
    `name`         VARCHAR(255) NOT NULL COMMENT '线路名称',
    `route_plan`   TEXT         NOT NULL COMMENT '线路规划',
    `route_hours`  SMALLINT     NOT NULL COMMENT '线路时长',
    `route_km`     SMALLINT     NOT NULL COMMENT '线路距离',
    `enable`       TINYINT      NOT NULL COMMENT '是否启用',
    `sort`         INT          NOT NULL COMMENT '排序',
    `description`  VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`    BIGINT                DEFAULT NULL COMMENT '创建者',
    `modify_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`    BIGINT                DEFAULT NULL COMMENT '修改者',
    `row_version`  INT                   DEFAULT NULL COMMENT '记录版本',
    `row_valid`    TINYINT               DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运输线路';

DROP TABLE IF EXISTS `db_tms`.`tb_transport_order`;
CREATE TABLE `db_tms`.`tb_transport_order`
(
    `id`                        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_num`                 VARCHAR(50) NOT NULL COMMENT '运输单号',
    `vin`                       VARCHAR(20) NOT NULL COMMENT '车架号',
    `carrier_code`              VARCHAR(50) NOT NULL COMMENT '承运商代码',
    `state`                     SMALLINT    NOT NULL COMMENT '订单状态',
    `transport_type_code`       VARCHAR(50) NOT NULL COMMENT '运输方式代码',
    `transport_route_code`      VARCHAR(50)          DEFAULT NULL COMMENT '运输线路代码',
    `start_logistics_node_code` VARCHAR(50)          DEFAULT NULL COMMENT '发车据点代码',
    `start_time`                TIMESTAMP            DEFAULT NULL COMMENT '发车时间',
    `end_logistics_node_code`   VARCHAR(50)          DEFAULT NULL COMMENT '接车据点代码',
    `estimated_end_time`        TIMESTAMP            DEFAULT NULL COMMENT '预计接车时间',
    `end_time`                  TIMESTAMP            DEFAULT NULL COMMENT '接车时间',
    `license_plate`             VARCHAR(100)         DEFAULT NULL COMMENT '运输车辆',
    `loading_line`              SMALLINT             DEFAULT NULL COMMENT '装载道',
    `id_number`                 VARCHAR(50)          DEFAULT NULL COMMENT '驾驶员身份证号',
    `dispatcher`                VARCHAR(100)         DEFAULT NULL COMMENT '调度人员',
    `dispatch_time`             TIMESTAMP            DEFAULT NULL COMMENT '调度时间',
    `lock`                      TINYINT              DEFAULT NULL COMMENT '是否锁定',
    `description`               VARCHAR(255)         DEFAULT NULL COMMENT '备注',
    `create_time`               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                 BIGINT               DEFAULT NULL COMMENT '创建者',
    `modify_time`               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_by`                 BIGINT               DEFAULT NULL COMMENT '修改者',
    `row_version`               INT                  DEFAULT NULL COMMENT '记录版本',
    `row_valid`                 TINYINT              DEFAULT NULL COMMENT '是否有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`order_num`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运输订单';