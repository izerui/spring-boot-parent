package com.yj2025.basic.dao.support;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;
import org.springframework.core.annotation.AliasFor;

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
public @interface Identical {

    /**
     * 对应实体类的字段名
     * @return
     */
    @AliasFor("columName")
    String value() default "";

    /**
     * 对应实体类的字段名
     * @return
     */
    @AliasFor("value")
    String columName() default "";

    /**
     * 冗余字段对应的实体类
     * @return
     */
    Class[] entity() default {};
}
