# 数据模型

## 实体关系

```
AdminUser 1──1 Merchant (通过 userId 关联)
Merchant  1──1 MerchantAccount (通过 merchantId 关联)
Merchant  1──N MerchantWithdraw (通过 merchantId 关联)
Merchant  1──N PayOrder (通过 pid 关联)
```

## 表结构

### admin_user

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| username | VARCHAR | 登录用户名 |
| password | VARCHAR | BCrypt 加密密码 |
| nickname | VARCHAR | 昵称 |
| avatar | VARCHAR | 头像 |
| email | VARCHAR | 邮箱 |
| phone | VARCHAR | 手机号 |
| role | VARCHAR | 角色: SUPER_ADMIN / ADMIN / VIEWER / MERCHANT |
| status | INT | 状态: 0-禁用 1-启用 |
| security_code | VARCHAR | 安全码(用于重置密码) |
| last_login_ip | VARCHAR | 最后登录 IP |
| last_login_time | DATETIME | 最后登录时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### merchant

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| merchant_key | VARCHAR | 商户密钥(用于签名验证) |
| name | VARCHAR | 商户名称 |
| status | INT | 状态: 0-禁用 1-启用 |
| user_id | BIGINT | 关联 admin_user.id |
| alipay_account | VARCHAR | 支付宝账号 |
| nick_name | VARCHAR | 姓名 |
| phone | VARCHAR | 手机号 |
| create_time | DATETIME | 创建时间 |

### merchant_account

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| merchant_id | BIGINT | 关联 merchant.id |
| total_income | DECIMAL | 累计收入 |
| available_balance | DECIMAL | 可用余额 |
| frozen_balance | DECIMAL | 冻结余额(提现审核中) |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### merchant_withdraw

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| merchant_id | BIGINT | 关联 merchant.id |
| amount | DECIMAL | 提现金额 |
| service_fee | DECIMAL | 手续费 (amount × rate) |
| amount_credited | DECIMAL | 实际到账金额 (amount - service_fee) |
| status | INT | 0-待处理 1-已完成 2-已拒绝 |
| remark | VARCHAR | 备注/拒绝原因 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### pay_order

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| out_trade_no | VARCHAR | 商户订单号 |
| trade_no | VARCHAR | 支付宝交易号 |
| subject | VARCHAR | 商品标题 |
| total_amount | DECIMAL | 订单金额 |
| status | INT | 0-待支付 1-已支付 2-已关闭 3-已退款 |
| pay_type | VARCHAR | 支付方式: alipay/wxpay |
| buyer_id | VARCHAR | 买家 ID |
| pid | BIGINT | 商户 ID |
| notify_url | VARCHAR | 异步通知地址 |
| return_url | VARCHAR | 同步跳转地址 |
| create_time | DATETIME | 创建时间 |
| pay_time | DATETIME | 支付时间 |
| notify_time | DATETIME | 通知时间 |

### payment_channel_config

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| channel_code | VARCHAR | 通道编码: ALIPAY / WECHAT |
| channel_name | VARCHAR | 通道名称 |
| config_data | TEXT | JSON 配置 (appId, privateKey, publicKey 等) |
| status | INT | 0-禁用 1-启用 |
| is_default | INT | 是否默认通道 |
| sort_order | INT | 排序 |
| remark | VARCHAR | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### system_config

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT AUTO_INCREMENT | 主键 |
| config_key | VARCHAR | 配置键 |
| config_value | VARCHAR | 配置值 |
| config_group | VARCHAR | 配置分组 |
| description | VARCHAR | 配置说明 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## DTO/VO 一览

| 类 | 用途 |
|---|---|
| `Result<T>` | 统一响应包装 `{code, message, data}` |
| `PageResult<T>` | 分页响应 `{records, total, page, size}` |
| `LoginRequest` | 登录请求 `{username, password}` |
| `LoginResponse` | 登录响应 `{token, username, nickname, role}` |
| `DashboardStatsVO` | 仪表盘统计 `{todayRevenue, todayOrders, successRate, totalMerchants}` |
| `OrderVO` | 订单详情 (PayOrder 字段 + merchantName) |
| `MerchantAccountVO` | 商户账户 (MerchantAccount 字段 + merchantName/alipayAccount/nickName/phone) |
| `WithdrawDetailVO` | 提现详情 (MerchantWithdraw 字段 + merchantName/alipayAccount/nickName/phone) |
