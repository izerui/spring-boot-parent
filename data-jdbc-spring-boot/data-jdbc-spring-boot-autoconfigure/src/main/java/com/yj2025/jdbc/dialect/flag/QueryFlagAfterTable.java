package com.yj2025.jdbc.dialect.flag;

import jdk.jfr.Description;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Description("data-jdbc生成的查询表后的一个标记,只对被该注解声明的方法有效,跳出方法后本地线程无法再获取值," +
        "建议该注解使用在RepositoryDao类的方法上,保证只对指定的某个查询方法使用。")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(QueryFlagAfterTables.class)
public @interface QueryFlagAfterTable {
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
     * 生效对应的表名前缀,如果为空则默认生效,否则匹配前缀生效
     *
     * @return
     */
    String tablePrefix() default "";

    Class holder() default QueryFlagThreadLocalHolder.class;

}
