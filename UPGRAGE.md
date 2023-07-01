* 增加`[basic-infrastructure-spring-boot]`模块, command和dao分别都依赖该模块
* DbContext 从`com.yj2025.basic.dao.support.DbContext`移动包位置到`com.yj2025.basic.support.DbContext`

自2023-06-28起:
参考: 
    https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
    https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x
* 版本 全局升级spring boot 依赖到 3.1.1（及相关cloud等其他依赖统一升级）
* 配置 [mvc-rest-spring-boot](mvc-rest-spring-boot) 移除`rest.cors.allowed`配置，默认全局支持跨域
* 依赖 `javax.validation` 替换成 `jakarta.validation`
* 依赖 `mysql:mysql-connector-java` 替换成 `com.mysql:mysql-connector-j`
* 依赖 jpa 规范升级到 3.1规范， `javax.persistence` 替换成 `jakarta.persistence`
* 配置 `server.max-http-header-size` 替换成 `server.max-http-request-header-size`
* 配置 `management.endpoints.web.exposure.include` 替换成 `management.endpoints.jmx.exposure.include`
* 配置 使用旧的`ObjectMapper`行为，不用新的隔离方式： 需要增加配置, `management.endpoints.jackson.isolated-object-mapper=false`
* 版本 `Micrometer` 导出器升级 到 1.11.1
* 配置 `spring.redis.` 替换成 `spring.data.redis`
* 版本 Spring Batch 升级到 5.0
* 依赖 `javax.servlet` 替换成 `jakarta.servlet`
* 依赖 `javax.persistence` 替换成 `jakarta.persistence`
* 依赖 `javax.annotation` 替换成 `jakarta.annotation`
* 移除 `ureport(javax.servlet)`
* 配置 自动装配模式改变 `spring.factories` 替换成 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`
* bean定义 全局使用 `jackson-spring-boot-starter` 模块中定义的 `ObjectMapper`
* 版本升级:
  * `spring-boot:2.7.5` -> `spring-boot:3.1.1`
  * `spring-cloud:2021.0.5` -> `spring-cloud:2021.0.5`
  * `org.mapstruct:1.5.2.Final` -> `org.mapstruct:1.5.5.Final`
  * `commons.text:1.9` -> `commons.text:1.10.0`
  * `easyexcel:3.1.1` -> `easyexcel:3.3.2`
  * `joda.time:2.10.2` -> `joda.time:2.12.5`
  * `lombok:1.18.24` -> `lombok:1.18.28`
  * `curator:5.4.0` -> `curator:5.5.0`
  * `guava:31.0.0-jre` -> `guava:32.0.0-jre`
  * `io.swagger.core.v3:swagger-annotations:2.2.7`  -> `io.swagger.core.v3:swagger-annotations:2.2.8`
  * `com.zaxxer:HikariCP:4.0.3` -> `com.zaxxer:HikariCP:5.0.1`
  * `mysql:mysql-connector-java:8.0.33` -> `com.mysql:mysql-connector-j:8.0.33`
  * `dom4j:dom4j:1.6.1` -> `org.dom4j:dom4j:2.1.4`
  * `io.github.izerui:work-weixin-spring-boot-starter:1.1.8` -> `io.github.izerui:work-weixin-spring-boot-starter:1.2.0`
  * `commons-validator:commons-validator:1.4.1` -> `commons-validator:commons-validator:1.7`
  * `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config:2.2.3.RELEASE` -> `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config:2022.0.0.0-RC2`
  * `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2.2.3.RELEASE` -> `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2022.0.0.0-RC2`
  * `com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel:2.2.3.RELEASE` -> `com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel:2022.0.0.0-RC2`
  * `io.micrometer:micrometer-registry-prometheus:1.6.1` -> `io.micrometer:micrometer-registry-prometheus:1.11.1`
  * `com.github.xiaoymin:knife4j-springdoc-ui:3.0.3` -> `com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.1.0`
* 移除依赖:
  * `micrometer-jvm-extras`
  * `apm-toolkit-micrometer-registry`
  * `ureport`