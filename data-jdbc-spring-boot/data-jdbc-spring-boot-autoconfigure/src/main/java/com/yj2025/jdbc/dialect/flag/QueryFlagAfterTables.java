package com.yj2025.jdbc.dialect.flag;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD})
@Documented
public @interface QueryFlagAfterTables {

    QueryFlagAfterTable[] value();
}