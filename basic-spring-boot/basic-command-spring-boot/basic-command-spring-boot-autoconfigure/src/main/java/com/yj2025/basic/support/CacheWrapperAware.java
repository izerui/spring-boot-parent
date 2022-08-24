package com.yj2025.basic.support;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.yj2025.basic.support.Context.tryWith;

public interface CacheWrapperAware<T extends Object> {
    /**
     * source字段名: {source字段值: target字段值}
     */
    ThreadLocal<Map<String, Map<Object, Object>>> THREAD_LOCAL = new InheritableThreadLocal<>();

    default T wrap(String sourceField, String targetField, Function<T, Object> nameValueGetter) {
        // 获取字段的值
        Function<String, Object> valueGetter = fieldName -> {
            Field field = tryWith(() -> getClass().getDeclaredField(fieldName));
            field.setAccessible(true);
            Object value = ReflectionUtils.getField(field, this);
            return value;
        };
        // 设置字段的值
        BiConsumer<String, Object> valueSetter = (fieldName, value) -> {
            Field field = tryWith(() -> getClass().getDeclaredField(fieldName));
            field.setAccessible(true);
            ReflectionUtils.setField(field, this, value);
        };

        Map<String, Map<Object, Object>> sourceFieldMap = THREAD_LOCAL.get();
        if (sourceFieldMap == null) {
            sourceFieldMap = new ConcurrentHashMap<>();
            THREAD_LOCAL.set(sourceFieldMap);
        }
        // 如果未设置当前源字段的sourceValue:targetValue map则初始化一个
        Map<Object, Object> valueMap = sourceFieldMap.get(sourceField);
        if (valueMap == null) {
            valueMap = new ConcurrentHashMap<>();
            sourceFieldMap.put(sourceField, valueMap);
        }
        // 源字段值
        Object sourceValue = valueGetter.apply(sourceField);
        if (sourceValue != null) { // 源字段值不为空，则开始获取目标值并设置到目标字段
            if (!valueMap.containsKey(sourceValue)) {
                Object targetValue = nameValueGetter.apply((T) this);
                if (targetValue != null) {
                    valueMap.put(sourceValue, targetValue);
                } else {
                    valueMap.put(sourceValue, null);
                }
            }
            valueSetter.accept(targetField, valueMap.get(sourceValue));
        }
        return (T) this;
    }
}
