package com.yj2025.jdbc.tenant;

import jdk.jfr.Description;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Description("将租户信息放入本地ThreadLocal中,在Entity中声明@Table(\"#{@tenantSharding.getTable('test_user')}\")即可实现根据租户动态分表")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TenantThreadLocal {
    String value();
    Class holder() default TenantThreadLocalHolder.class;
}
