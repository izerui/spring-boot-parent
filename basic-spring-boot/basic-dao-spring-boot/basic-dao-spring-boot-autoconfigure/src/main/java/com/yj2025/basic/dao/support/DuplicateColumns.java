package com.yj2025.basic.dao.support;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD})
@Documented
public @interface DuplicateColumns {

    DuplicateColumn[] value();
}
