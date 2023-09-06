package com.yj2025.tenant;

import jdk.jfr.Description;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Description("""
        将租户信息放入本地ThreadLocal中
        * 支持通过spring.datasource.tenant.* 配置动态分库。
        * 支持在Entity中声明@Table(\"#{@tenantSharding.getTable('test_user')}\")即可实现根据租户动态分表
        """)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Tenant {
    /**
     * 返回 spel表达式结果将作为tenantId, 将会放入 TenantHolder 中
     * @return
     */
    String value();
    Class holder() default TenantHolder.class;
}
