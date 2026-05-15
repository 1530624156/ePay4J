# API 端点清单

## 公开接口（无需认证）

### 支付宝直连 `/api/alipay/**`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/alipay/pagePay?subject=...&totalAmount=...` | 电脑网站支付 |
| GET | `/api/alipay/wapPay?subject=...&totalAmount=...` | 手机网站支付 |
| GET | `/api/alipay/query?outTradeNo=...` | 查询订单 |
| POST | `/api/alipay/refund` | 退款 `{outTradeNo, refundAmount}` |
| POST | `/api/alipay/notify` | 异步通知回调 |
| GET | `/api/alipay/return` | 同步跳转回调 |

### 易支付商户接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET/POST | `/submit.php` | 页面跳转支付 |
| POST | `/mapi.php` | API 接口支付 |
| GET/POST | `/api.php?act=...` | 管理接口 (query/order/orders/refund/types) |

### 站点设置

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/settings` | 返回 site_name, pay_api_url |

### 商户登录

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/merchant/auth/login` | 商户登录 `{username, password}` |

### 管理员认证

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/admin/auth/login` | 管理员登录 `{username, password}` |
| POST | `/api/admin/auth/reset-password` | 重置密码 `{username, securityCode, newPassword}` |

---

## 管理后台接口（需 ROLE_SUPER_ADMIN）

> 所有请求头需携带 `Authorization: Bearer <token>`

### 认证 `/api/admin/auth`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/auth/info` | 获取当前管理员信息 |
| POST | `/api/admin/auth/logout` | 登出 |
| PUT | `/api/admin/auth/password` | 修改密码 `{oldPassword, newPassword}` |

### 仪表盘 `/api/admin/dashboard`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/dashboard/stats` | 统计概览 (今日收入/订单/成功率/商户数) |
| GET | `/api/admin/dashboard/revenue-chart` | 收入趋势图 |
| GET | `/api/admin/dashboard/order-status-chart` | 订单状态分布 |
| GET | `/api/admin/dashboard/recent-orders` | 最近订单 |

### 商户管理 `/api/admin/merchants`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/merchants/page?page=&size=&...` | 分页查询 |
| GET | `/api/admin/merchants/{id}` | 商户详情 |
| POST | `/api/admin/merchants` | 创建商户 `{name, alipayAccount, nickName, phone}` |
| PUT | `/api/admin/merchants/{id}` | 更新商户 |
| PUT | `/api/admin/merchants/{id}/status` | 启用/禁用 `{status}` |
| POST | `/api/admin/merchants/{id}/reset-key` | 重置商户密钥 |
| DELETE | `/api/admin/merchants/{id}` | 删除商户 |

### 商户账户 `/api/admin/merchant-accounts`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/merchant-accounts/{merchantId}` | 查看商户账户 |

### 订单管理 `/api/admin/orders`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/orders/timeout` | 获取订单超时配置 |
| GET | `/api/admin/orders?page=&size=&...` | 分页查询 (支持 outTradeNo/status/payType/merchantId/merchantName/startDate/endDate 筛选) |
| GET | `/api/admin/orders/{id}` | 订单详情 |
| POST | `/api/admin/orders/{id}/refund` | 退款 |
| POST | `/api/admin/orders/{id}/close` | 关闭订单 |
| DELETE | `/api/admin/orders/{id}` | 删除订单 |
| DELETE | `/api/admin/orders/batch?ids=1,2,3` | 批量删除 |

### 通道管理 `/api/admin/channels`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/channels` | 通道列表 |
| GET | `/api/admin/channels/{id}` | 通道详情 |
| POST | `/api/admin/channels` | 创建通道 |
| PUT | `/api/admin/channels/{id}` | 更新通道 |
| PUT | `/api/admin/channels/{id}/status` | 启用/禁用 |
| POST | `/api/admin/channels/{id}/test` | 测试通道连通性 |
| DELETE | `/api/admin/channels/{id}` | 删除通道 |

### 用户管理 `/api/admin/users`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/users/page?page=&size=&...` | 分页查询 |
| POST | `/api/admin/users` | 创建用户 `{username, password, nickname, role}` |
| PUT | `/api/admin/users/{id}` | 更新用户 |
| PUT | `/api/admin/users/{id}/status` | 启用/禁用 |
| POST | `/api/admin/users/{id}/reset-password` | 重置密码 |
| DELETE | `/api/admin/users/{id}` | 删除用户 |

### 提现管理 `/api/admin/withdraw`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/withdraw/page?page=&size=&...` | 分页查询 (支持 merchantId/merchantName/status 筛选) |
| GET | `/api/admin/withdraw/{id}` | 提现详情 (含商户信息) |
| POST | `/api/admin/withdraw/{id}/approve` | 审批通过 |
| POST | `/api/admin/withdraw/{id}/reject` | 审批拒绝 `{reason}` |

### 系统配置 `/api/admin/system`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/system/config` | 获取全部配置 |
| PUT | `/api/admin/system/config` | 批量更新配置 `[{configKey, configValue}, ...]` |

---

## 商户接口（需 ROLE_MERCHANT）

> 所有请求头需携带 `Authorization: Bearer <token>`

### 商户信息 `/api/merchant`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/merchant/info` | 获取商户信息 |
| PUT | `/api/merchant/info` | 更新信息 `{alipayAccount, nickName, phone}` |
| GET | `/api/merchant/account` | 获取账户余额 |
| PUT | `/api/merchant/password` | 修改密码 `{oldPassword, newPassword}` |
| GET | `/api/merchant/credentials` | 获取 API 凭证 (pid, key) |
| POST | `/api/merchant/reset-key` | 重置商户密钥 |

### 订单查询 `/api/merchant/orders`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/merchant/orders?page=&size=` | 订单列表 |
| GET | `/api/merchant/orders/{id}` | 订单详情 |

### 提现 `/api/merchant/withdraw`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/merchant/withdraw` | 申请提现 `{amount}` |
| GET | `/api/merchant/withdraw/records?page=&size=` | 提现记录 |
