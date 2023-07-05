package com.yj2025.jdbc.sharding;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Label("数据库分表")
@Category("Entity")
@Description("表示当前Entity是否分表")
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingTable {
    String sourceTable() default "";
}
