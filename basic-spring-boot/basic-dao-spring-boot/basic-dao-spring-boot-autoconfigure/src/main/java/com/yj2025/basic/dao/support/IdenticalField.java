package com.yj2025.basic.dao.support;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Label("冗余字段")
@Category("Entity")
@Description("表示所冗余的字段信息")
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = IdenticalFields.class)
public @interface IdenticalField {

    /**
     * 对应实体类的字段名,默认为当前属性名
     *
     * @return
     */
    String column() default "";


    /**
     * 冗余字段对应的实体类
     *
     * @return
     */
    Class entity();
}
