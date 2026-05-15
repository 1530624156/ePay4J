# EPay 支付网关 Agent Guide

## 项目概述

EPay 是一个基于 Spring Boot 2.7 的易支付协议兼容支付网关，采用密钥模式对接支付宝。为个人网站站点提供收款渠道，支持支付宝网页/手机支付。

## 技术栈

- Java 8 + Spring Boot 2.7.18
- Spring Security + JWT (HMAC-SHA256, 24h 过期)
- MyBatis-Plus 3.5.5 + MySQL 8.0
- 支付宝 SDK 4.39.218.ALL
- 端口: 8888

## 模块划分

| 模块 | 路径前缀 | 认证方式 | 说明 |
|---|---|---|---|
| 公开支付 API | `/submit.php`, `/mapi.php`, `/api.php`, `/api/alipay/**` | 商户签名 (MD5) | 易支付协议 + 支付宝直连 |
| 管理后台 | `/api/admin/**` | JWT (ROLE_SUPER_ADMIN) | 仪表盘、商户/订单/通道/提现管理 |
| 商户自助 | `/api/merchant/**` | JWT (ROLE_MERCHANT) | 信息管理、订单查询、提现申请 |
| 公开设置 | `/api/settings` | 无需认证 | 返回站点名称和支付接口地址 |

## 数据库

7 张表: `admin_user`, `merchant`, `merchant_account`, `merchant_withdraw`, `pay_order`, `payment_channel_config`, `system_config`

## 快速链接

- [架构总览](docs/architecture/index.md) — 分层规则、包结构、数据流
- [API 端点清单](docs/architecture/api-endpoints.md) — 全部接口列表
- [隐性业务约定](docs/architecture/implicit-contracts.md) — 业务流程、签名规则、注意事项
- [数据模型](docs/architecture/data-model.md) — 实体与数据库表结构
- [开发指南](docs/standards/development.md) — 构建、测试、代码规范
