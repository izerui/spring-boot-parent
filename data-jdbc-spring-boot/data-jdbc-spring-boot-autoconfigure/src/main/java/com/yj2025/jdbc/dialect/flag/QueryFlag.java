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
     * 会在自动生成的sql的表名后跟随spel解析后的内容
     *
     * @return
     */
    String value() default "";

    /**
     * 是否使用注释包裹起来
     *
     * @return
     */
    boolean isComment() default true;

    /**
     * 前面是否增加一个空格
     *
     * @return
     */
    boolean isPreWhitespace() default true;

    Class holder() default QueryFlagThreadLocalHolder.class;
}
