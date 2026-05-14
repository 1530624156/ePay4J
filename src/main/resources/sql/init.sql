CREATE DATABASE IF NOT EXISTS `epay` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `epay`;

-- 支付订单表
CREATE TABLE `pay_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `out_trade_no` VARCHAR(64) NOT NULL COMMENT '商户订单号',
    `trade_no` VARCHAR(64) DEFAULT NULL COMMENT '支付宝交易号',
    `subject` VARCHAR(256) NOT NULL COMMENT '订单标题',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额（元）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已关闭 3-已退款',
    `pay_type` VARCHAR(32) DEFAULT NULL COMMENT '支付方式：PAGE-电脑网站 WAP-手机网站',
    `buyer_id` VARCHAR(64) DEFAULT NULL COMMENT '买家支付宝用户ID',
    `pid` BIGINT DEFAULT NULL COMMENT '商户ID（易支付订单时有值）',
    `notify_url` VARCHAR(512) DEFAULT NULL COMMENT '商户异步通知地址',
    `return_url` VARCHAR(512) DEFAULT NULL COMMENT '商户同步跳转地址',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `notify_time` DATETIME DEFAULT NULL COMMENT '通知时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
    KEY `idx_trade_no` (`trade_no`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- 商户表
CREATE TABLE `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商户ID（即pid）',
    `merchant_key` VARCHAR(64) NOT NULL COMMENT 'API密钥',
    `name` VARCHAR(128) NOT NULL COMMENT '商户名称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1-启用 0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';
