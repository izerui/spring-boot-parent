package com.yj2025.jdbc.dialect.flag;

import jdk.jfr.Description;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Description("data-jdbc生成的查询表后的一个标记")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface QueryFlag {
    /**
     * 内容会以注释的形式跟随在查询的table后面
     * @return
     */
    String value();
    Class holder() default QueryFlagThreadLocalHolder.class;
}
