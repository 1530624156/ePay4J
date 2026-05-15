# 隐性业务约定

## 关键业务流程

### 支付流程

```
商户发起支付 → 签名验证(MD5) → 创建订单 → 路由到支付通道 → 支付宝支付 → 异步通知 → 更新订单状态 → 回调商户 notifyUrl
```

### 商户签名规则

将请求参数按 key 字母排序后拼接，末尾追加 merchantKey，整体 MD5 加密：

```
sign = MD5(key1=value1&key2=value2&...&merchantKey)
```

### 提现流程

```
商户申请提现 → 校验信息完整性 → 校验可用余额 → 计算手续费(amount × rate) → 冻结金额 → 管理员审批
  ├─ 通过 → 扣除冻结余额
  └─ 拒绝 → 冻结余额退回可用余额
```

- 手续费 = 提现金额 × 系统配置中的 `rate`
- 实际到账金额 = 提现金额 - 手续费
- 提现前商户必须维护姓名、手机号、支付宝账号

### 商户创建流程

```
管理员创建商户 → 自动生成 AdminUser(ROLE_MERCHANT) → 创建 Merchant 记录 → 创建 MerchantAccount
```

删除商户时级联删除关联的 AdminUser。

### 订单超时

`OrderTimeoutTask` 每 60 秒执行，读取 `system_config` 中的 `order_timeout_minutes`（默认 30），将超时的待支付订单(status=0)自动关闭(status=2)。

## 安全规则

- `/api/admin/**` 除 login/reset-password 外，JWT 过滤器直接返回 401
- `/api/merchant/**` 通过 SecurityConfig `hasRole("MERCHANT")` 控制
- 支付宝异步通知需验证签名（`AlipaySignature.rsaCheckV1`）
- 管理员密码 BCrypt 加密存储

## 数据一致性

- 支付成功回调：更新订单状态 + 商户账户累计收入和可用余额（在同一事务中）
- 提现审批通过：扣减冻结余额
- 提现审批拒绝：冻结余额退回可用余额
- `AdminChannelService` 修改支付宝通道配置时自动刷新 `AlipayClientHolder`

## 配置项约定（system_config 表）

| configKey | 说明 | 示例 |
|---|---|---|
| `site_name` | 站点名称 | EPay |
| `pay_api_url` | 支付接口地址 | https://pay.example.com |
| `rate` | 提现费率 | 0.01 |
| `order_timeout_minutes` | 订单超时时间(分钟) | 30 |

## 注意事项

- 支付宝配置存储在 `payment_channel_config` 表，通过 `AlipayClientHolder` 懒加载获取
- `PaymentChannel` 枚举定义了 ALIPAY/WECHAT/PAYPAL，但当前仅实现支付宝策略
- JWT Secret 在 `application.yml` 中配置，生产环境必须更换
- CORS 配置允许所有来源 (`/api/**`)
- MyBatis-Plus 分页需配合 `MyBatisPlusConfig` 中的分页插件
