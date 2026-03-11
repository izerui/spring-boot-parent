# yj2025-spring-boot-parent

Spring Boot 基础模块封装，为业务开发提供统一的基础设施和最佳实践。

## 简介

yj2025-spring-boot-parent 是一个企业级 Spring Boot 基础模块封装项目，提供了涵盖各个技术栈的 starter 模块，帮助开发者快速构建稳定的微服务应用。所有模块均采用starter模式，引入即用，无需繁琐配置。

## 快速开始

### Maven 引入

```xml
<dependency>
    <groupId>com.yj2025</groupId>
    <artifactId>xxx-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

### 版本说明

当前版本：`3.3.3-SNAPSHOT`

请根据 Spring Boot 版本选择对应的模块版本。

## 模块列表

### 数据层

| 模块 | 说明 |
|------|------|
| mybatis-spring-boot | 基于 MyBatis-Plus 的 ORM 封装 |
| jpa-spring-boot | Spring Data JPA 封装，支持 AOP |
| data-jdbc-spring-boot | Spring Data JDBC 封装 |
| dynamic-datasource-spring-boot | 动态数据源切换 |
| sharding-table-spring-boot | 分表解决方案 |
| mongo-transaction-spring-boot | MongoDB 事务支持（需 MongoDB 4.2+ 集群模式）|
| tenant-spring-boot | 多租户支持 |

### 缓存与消息

| 模块 | 说明 |
|------|------|
| redis-spring-boot | Redis 封装 |
| rabbit-spring-boot | RabbitMQ 封装 |
| kafka-spring-boot | Kafka 封装 |
| lock-spring-boot | 分布式全局锁、延时器、计数器 |

### 服务治理

| 模块 | 说明 |
|------|------|
| sentinel-spring-boot | 分布式哨兵：限流、熔断 |
| nacos-spring-boot | Nacos 注册中心、配置中心 |
| job-spring-boot | 分布式调度 |

### Web 与网络

| 模块 | 说明 |
|------|------|
| mvc-rest-spring-boot | 全局异常处理：Feign 调用、Web 请求、响应封装 |
| doc-spring-boot | Swagger API 封装 |
| websocket-spring-boot | WebSocket 服务封装 |
| proxy-spring-boot | 请求代理：网关代理、认证服务 |
| open-spring-boot | 开放平台服务代理：网关、授权服务 |

### 云服务与第三方

| 模块 | 说明 |
|------|------|
| amazonaws-spring-boot | AWS 云服务相关 |
| weixin-spring-boot | 企业微信封装：SDK、回调 |
| sms-spring-boot | 短信相关 SDK 封装 |
| chatgpt-spring-boot | ChatGPT API 封装 |

### 监控与运维

| 模块 | 说明 |
|------|------|
| metrics-spring-boot | 基于 Spring Actuator 的状态监控，暴露 Prometheus 端点 |
| audit-spring-boot | 请求 Audit 审计日志 |
| elasticsearch-logging-spring-boot | Elasticsearch 日志方案 |

### 基础设施

| 模块 | 说明 |
|------|------|
| customizer-spring-boot | Spring 自定义 Bean 扩展、自定义 Bean 加载 |
| high-performance-spring-boot | 高性能模块：批处理、多线程 |
| batch-processor-spring-boot | 批处理器 |
| sequence-number-spring-boot | 顺序号池：支持按年/月/日，可回收、可验证 |
| platform-commons | 平台公共工具类 |

### 其他

| 模块 | 说明 |
|------|------|
| jackson-spring-boot | Jackson 序列化/反序列化扩展 |
| mail-spring-boot | 邮件服务封装 |
| cloud-file-spring-boot | 云文件服务封装 |
| validator-spring-boot | 参数校验扩展 |
| table-creator-spring-boot | 表结构创建工具 |

## 使用示例

### MyBatis-Plus 快速开始

```java
@SpringBootApplication
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: root
```

引入依赖后，自动配置完成，直接使用 MyBatis-Plus 的 CRUD 操作。

### Redis 使用

```java
@Autowired
private StringRedisTemplate redisTemplate;

redisTemplate.opsForValue().set("key", "value");
```

### 分布式锁

```java
@Autowired
private LockTemplate lockTemplate;

lockTemplate.execute("lockKey", 3000, () -> {
    // 业务逻辑
});
```

## 升级说明

详细升级指南请参考 [UPGRADE.md](./UPGRADE.md)

## 更新日志

详细更新日志请参考 [CHANGELOG.md](./CHANGELOG.md)

## 贡献指南

欢迎提交 Pull Request，请确保提交信息符合规范。

## 许可证

MIT License
