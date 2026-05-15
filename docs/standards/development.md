# 开发指南

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0

## 构建与运行

```bash
mvn clean package -DskipTests   # 构建
mvn spring-boot:run              # 运行 (端口 8888)
mvn test                         # 运行测试
```

## 数据库初始化

```bash
mysql -u root -p < src/main/resources/sql/init.sql
mysql -u root -p < src/main/resources/sql/init_admin.sql
```

## 代码规范

- 使用 Lombok `@Data` 减少样板代码
- MyBatis-Plus 查询统一使用 `LambdaQueryWrapper`（类型安全）
- Controller 返回统一使用 `Result<T>` 包装
- 分页返回使用 `PageResult<T>` 包装
- Controller 中禁止使用 `@Transactional`
- 依赖注入使用 `@Autowired`

## 分层职责

| 层 | 职责 | 禁止 |
|---|---|---|
| Controller | 参数校验、调用 Service、返回 Result | 业务逻辑、数据库操作 |
| Service | 业务逻辑、事务管理 | 直接操作 HttpServletResponse |
| Mapper | 数据库 CRUD | 复杂业务逻辑 |

## 安全要求

- 禁止硬编码密钥，敏感配置通过 `application.yml` 或数据库 `payment_channel_config` 管理
- 密码使用 BCrypt 加密
- 商户密钥支持定期轮换 (`reset-key` 接口)
- JWT Secret 生产环境必须更换

## 异常处理

- 业务异常抛 `BusinessException`，由 `GlobalExceptionHandler` 统一处理
- 参数校验异常抛 `IllegalArgumentException`，返回 400
- 未知异常返回 500 + 通用提示

## 扩展支付通道

1. 在 `PaymentChannel` 枚举中添加条目
2. 实现 `PaymentChannelStrategy` 接口
3. 在 `payment_channel_config` 表中插入通道配置
4. `PaymentChannelFactory` 会自动发现并注册新策略
