package com.yj2025.basic.support;

import jdk.jfr.Description;

import java.lang.annotation.*;

@Description("用来标记入参可能会被")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ParamMayBeModified {
}
