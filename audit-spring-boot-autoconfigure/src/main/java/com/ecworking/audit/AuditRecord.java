package com.ecworking.audit;

import java.lang.annotation.*;

/**
 * Created by serv on 2016/12/8.
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditRecord {
    /** 当前操作的名字 */
    String value() default "";
}
