package com.yj2025.mybatis.toolkit;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> T getPropertyValue(Class targetClass, Object target, String property) {
        Field field = ReflectionUtils.findField(targetClass, property);
        field.setAccessible(true);
        return (T) ReflectionUtils.getField(field, target);
    }

    public static void setPropertyValue(Class targetClass, Object target, String property, Object value) {
        Field field = ReflectionUtils.findField(targetClass, property);
        field.setAccessible(true);
        ReflectionUtils.setField(field,target,value);
    }
}
