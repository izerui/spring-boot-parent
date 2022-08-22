package com.yj2025.basic.support;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public interface DeepObserverAware {

    /**
     * 自上而下，观察当前及所有成员变量，当实例为指定类对象的时候，触发观察事件
     *
     * @param interfaceType 指定接口
     * @param consumer
     * @param <T>
     */
    default <T> void subscribeOutGoing(Class<T> interfaceType, Consumer<T> consumer) {
        Class<?>[] interfaces = this.getClass().getInterfaces();
        boolean anyMatch = Arrays.stream(interfaces).anyMatch(aClass -> aClass.isAssignableFrom(interfaceType));
        if (anyMatch) {
            consumer.accept((T) this);
        }
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(declaredField, this);
            if (fieldValue != null && fieldValue instanceof DeepObserverAware) {
                ((DeepObserverAware) fieldValue).subscribeOutGoing(interfaceType, consumer);
            }
        }
    }

    /**
     * 自下而上，观察所有成员变量及当前，当实例为指定类对象的时候，触发观察事件
     *
     * @param interfaceType 指定接口
     * @param consumer
     * @param <T>
     */
    default <T> void subscribeIncoming(Class<T> interfaceType, Consumer<T> consumer) {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(declaredField, this);
            if (fieldValue != null && fieldValue instanceof DeepObserverAware) {
                ((DeepObserverAware) fieldValue).subscribeIncoming(interfaceType, consumer);
            }
        }
        Class<?>[] interfaces = this.getClass().getInterfaces();
        boolean anyMatch = Arrays.stream(interfaces).anyMatch(aClass -> aClass.isAssignableFrom(interfaceType));
        if (anyMatch) {
            consumer.accept((T) this);
        }
    }

}
