package com.yj2025.mybatis.toolkit;

import org.springframework.util.ReflectionUtils;
import sun.misc.Unsafe;

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
        try {
            Unsafe unsafe = (Unsafe) field.get(null);
            long offset = unsafe.objectFieldOffset(field);
            unsafe.putObject(target, offset, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
//        ReflectionUtils.setField(field,target,value);
    }
}
