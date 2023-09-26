# 分表组件

1. 引入依赖
```
<dependency>
    <groupId>com.yj2025</groupId>
    <artifactId>sharding-table-spring-boot-starter</artifactId>
</dependency>
```

2. 在执行入口增加注解 `@Tenant(value = "#{#entCode}", year = "#{#year}")`, 如果不使用按租户的基础上再按年度分，则不用声明 `year` 属性
```
@Tenant("#{#entCode}")
public List<TestUser> findList(String entCode) {
    // ...
}
```

或者手动放入本地线程值:

```
TenantHolder.setTenantId
TenantHolder.setYear
```

3. 在dao层的@Query中指定分表逻辑并且在entity中相同的指定

entity:
```
按租户:
@Data
@Table("#{@sharding.getTable('test_user')}")
public class TestUser {

按租户+年度:
@Data
@Table("#{@sharding.getYearTable('test_user')}")
public class TestUser {
```

repository:
```
按租户:
@Query("select * from #{@sharding.getTable('test_user')} where code like CONCAT('%', :code,'%') ")
List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);
按租户+年度:
@Query("select * from #{@sharding.getYearTable('test_user')} where code like CONCAT('%', :code,'%') ")
List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);
```

4. 则会在执行sql查询的时候，动态通过spel表达式将表名替换成指定的新的表名