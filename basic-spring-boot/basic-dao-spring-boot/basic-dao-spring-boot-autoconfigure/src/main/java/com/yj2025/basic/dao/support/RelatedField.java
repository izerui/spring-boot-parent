package com.yj2025.basic.dao.support;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author liuyuhua
 */
@Label("关联关系")
@Category("Entity")
@Description("表示所对应的外表的关联关系")
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RelatedField {

    /**
     * 关联实体类的字段
     * @return
     */
    @AliasFor("primaryKey")
    String value() default "recordId";

    /**
     * 关联实体类的主键名
     * @return
     */
    @AliasFor("value")
    String primaryKey() default "recordId";

    /**
     * 关联的实体类
     * @return
     */
    Class[] entity() default {};
}
