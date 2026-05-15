# 架构总览

## 分层架构

```
Controller → Service → Mapper → MySQL
                ↓
           Channel (策略模式)
           Utils / Task
```

- **Controller**: REST 入口，处理 HTTP 请求，返回统一响应 `Result<T>`
- **Service**: 业务逻辑层，核心业务处理
- **Mapper**: MyBatis-Plus 数据库映射接口，继承 `BaseMapper<T>`
- **Channel**: 支付通道策略模式，通过 `PaymentChannelFactory` 路由
- **Task**: 定时任务（订单超时关闭）

## 包结构

```
com.mavis
├── controller/                    # 公开 API (AlipayController, EpayController, SettingsController)
├── model/
│   ├── admin/
│   │   ├── controller/            # 管理后台 API (9 个 Controller)
│   │   ├── service/               # 管理后台业务逻辑 (9 个 Service)
│   │   └── dto/                   # 数据传输对象 (Result, PageResult, VO 等)
│   └── merchant/
│       ├── controller/            # 商户 API (4 个 Controller)
│       └── service/               # 商户业务逻辑 (2 个 Service)
├── entity/                        # MyBatis-Plus 实体类 (7 个)
├── mapper/                        # MyBatis Mapper 接口 (7 个)
├── channel/                       # 支付通道策略模式
│   ├── PaymentChannelStrategy     # 策略接口
│   ├── PaymentChannelFactory      # 策略工厂
│   └── impl/AlipayChannelStrategy # 支付宝实现
├── security/                      # Spring Security + JWT
│   ├── SecurityConfig             # 安全配置 (URL 权限规则)
│   ├── JwtAuthenticationFilter    # JWT 过滤器
│   └── JwtTokenProvider           # JWT 工具
├── config/                        # Spring 配置类
│   ├── AlipayClientHolder         # 支付宝客户端持有者
│   ├── JwtConfig                  # JWT 配置绑定
│   ├── MyBatisPlusConfig          # 分页插件
│   └── WebMvcConfig               # CORS 配置
├── common/
│   ├── constant/                  # 常量 (Constants, PaymentChannel 枚举)
│   └── exception/                 # 异常处理 (BusinessException, GlobalExceptionHandler)
├── service/                       # 核心服务 (AlipayService, EpayService, MerchantNotifyService)
├── task/                          # 定时任务 (OrderTimeoutTask)
└── util/                          # 工具类 (PayUtils)
```

## 认证机制

| 场景 | 方式 | 说明 |
|---|---|---|
| 商户支付接口 | MD5 签名验证 | 参数按 key 排序拼接 + merchantKey，MD5 加密 |
| 管理后台 | JWT Token | BCrypt 密码登录，Bearer Token 访问 |
| 商户自助 | JWT Token | BCrypt 密码登录，Bearer Token 访问 |

## 支付通道策略模式

```
PaymentChannelStrategy (接口)
├── getChannelCode()
├── pagePay() / wapPay()
├── query() / refund() / close()
└── handleNotify()

PaymentChannelFactory
└── 按 channelCode 路由到具体策略

AlipayChannelStrategy
└── 委托 AlipayService 实现
```

当前仅实现支付宝通道，扩展微信支付需新增 `WechatChannelStrategy` 实现类。
