# 动态数据源

1. 引入依赖
```
<dependency>
    <groupId>com.yj2025</groupId>
    <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
</dependency>
```

2. 配置中增加指定租户的数据源
```
spring.datasource.tenant.datasource.sharding001.url=jdbc:mysql://10.96.15.155:3306/bboss?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false
spring.datasource.tenant.datasource.sharding001.username=yunji
spring.datasource.tenant.datasource.sharding001.password=123456
spring.datasource.tenant.datasource.sharding001.pool-name=sharding001
spring.datasource.tenant.datasource.sharding001.hikari.maximum-pool-size=20
spring.datasource.tenant.datasource.sharding001.hikari.minimum-idle=10
spring.datasource.tenant.datasource.sharding001.hikari.idle-timeout=600000
spring.datasource.tenant.datasource.sharding001.hikari.is-auto-commit=true
spring.datasource.tenant.datasource.sharding001.hikari.max-lifetime=1800000
spring.datasource.tenant.datasource.sharding001.hikari.connection-timeout=30000
```

3. 代码入口增加`@Tenant("#{#entCode}")`指定本地线程放入租户信息
```
@Tenant("#{#entCode}")
public void testMaster(String entCode) {
    // ... 
}
```

4. 则当前微服务会自动根据当前请求入口的租户，在使用数据源的时候自动判断如果有配置当前租户的数据源，则自动使用。