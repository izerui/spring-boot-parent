package com.yj2025.basic.dao.support;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;

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
@Repeatable(value = RelatedColumn.RelatedColumns.class)
public @interface RelatedColumn {

    /**
     * 关联实体类的字段,默认为 recordId
     *
     * @return
     */
    String column() default "recordId";

    /**
     * 关联的实体类
     *
     * @return
     */
    Class entity();

    /**
     * @author liuyuhua
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Target({ElementType.FIELD})
    @Documented
    @interface RelatedColumns {

        RelatedColumn[] value();
    }
}
