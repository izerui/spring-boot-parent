package com.yj2025.jdbc.tenant;

import jdk.jfr.Description;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Description("将租户信息放入本地ThreadLocal中")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TenantThreadLocal {
    String value() default "#{#entCode}";
    Class holder() default TenantThreadLocalHolder.class;
}
