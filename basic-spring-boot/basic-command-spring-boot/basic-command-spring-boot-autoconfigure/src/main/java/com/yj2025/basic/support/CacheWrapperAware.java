package com.yj2025.basic.support;

import io.vavr.control.Option;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.yj2025.basic.support.Context.tryWith;

/**
 * 缓存结果，并重复利用，避免多次获取缓存结果。
 * 适合结果集内容补全逻辑。例如：根据分类号设置分类名称等等。
 * 注意： sourceField 和 methodName 尽量保证其值对象为基本类型：String、Integer等。否则无法达到缓存多次利用的目的（或者覆盖equals方法）。
 *
 * @param <T>
 */
public interface CacheWrapperAware<T extends Object> {
    /**
     * 当使用 {{@link #wrapByField}}时表示：
     * {
     * 源字段名: {
     * 源字段值: 缓存
     * }
     * }
     * <p>
     * 当使用 {{@link #wrapByMethod}}时表示：
     * {
     * 方法名: {
     * 方法返回值: 缓存
     * }
     * }
     */
    ThreadLocal<Map<String, Map<Object, Object>>> THREAD_LOCAL = new InheritableThreadLocal<>();


    /**
     * 从本地线程中查找缓存map
     */
    Function<String, Map<Object, Object>> mapGetter = key -> {
        Map<String, Map<Object, Object>> threadLocalMap = THREAD_LOCAL.get();
        if (threadLocalMap == null) {
            threadLocalMap = new ConcurrentHashMap<>();
            THREAD_LOCAL.set(threadLocalMap);
        }
        // 如果找不到当前key对应的map，则创建一个
        Map<Object, Object> cacheMap = threadLocalMap.get(key);
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<>();
            threadLocalMap.put(key, cacheMap);
        }
        return cacheMap;
    };

    /**
     * 每次调用该方法： 则获取字段`sourceField`的值(非空执行后续)，然后通过`THREAD_LOCAL`获取该值对应的缓存，
     * 如果缓存在直接将缓存赋值给字段`targetField`，否则调用`targetValueGetter`获取并缓存起来，然后再次赋值`targetField`
     *
     * @param sourceField 源字段名
     * @param targetField 目标字段名
     * @param cacheGetter 获取缓存的方法
     * @return 返回当前对象
     */
    @SuppressWarnings("unchecked")
    default T wrapByField(String sourceField, String targetField, Function<T, Object> cacheGetter) {
        // 获取字段的值
        Function<String, Object> valueGetter = fieldName -> {
            Field field = tryWith(() -> getClass().getDeclaredField(fieldName));
            field.setAccessible(true);
            return ReflectionUtils.getField(field, this);
        };
        // 设置字段的值
        BiConsumer<String, Object> valueSetter = (fieldName, value) -> {
            Field field = tryWith(() -> getClass().getDeclaredField(fieldName));
            field.setAccessible(true);
            ReflectionUtils.setField(field, this, value);
        };
        // 源字段值非空情况下
        Option.of(valueGetter.apply(sourceField))
                .peek(sourceValue -> {
                    // 缓存map
                    Map<Object, Object> cacheMap = mapGetter.apply(sourceField);
                    if (!cacheMap.containsKey(sourceValue)) {
                        Object targetValue = cacheGetter.apply((T) this);
                        cacheMap.put(sourceValue, targetValue);
                    }
                    valueSetter.accept(targetField, cacheMap.get(sourceValue));
                });
        return (T) this;
    }

    /**
     * 每次调用该方法： 则根据`methodName`获取返回值(非空执行后续)，然后通过`THREAD_LOCAL`获取该返回值对应的缓存，
     * 如果缓存在直接调用`targetConsumer`，否则调用`targetValueGetter`获取并缓存起来，然后再次调用`targetConsumer`
     *
     * @param methodName     当前类的任一`get`方法
     * @param cacheGetter    获取缓存的方法
     * @param targetConsumer 消费缓存
     * @param <R>            缓存
     * @return 当前对象
     */
    @SuppressWarnings("unchecked")
    default <R> T wrapByMethod(String methodName, Function<T, R> cacheGetter, Consumer<R> targetConsumer) {
        // 获取方法返回值
        Function<String, R> methodValueGetter = method -> {
            Method method1 = tryWith(() -> getClass().getDeclaredMethod(method));
            method1.setAccessible(true);
            return (R) ReflectionUtils.invokeMethod(method1, this);
        };
        // 方法返回值非空情况下
        Option.of(methodValueGetter.apply(methodName))
                .peek(sourceValue -> {
                    // 缓存map
                    Map<Object, Object> cacheMap = mapGetter.apply(methodName);
                    if (!cacheMap.containsKey(sourceValue)) {
                        R targetValue = cacheGetter.apply((T) this);
                        cacheMap.put(sourceValue, targetValue);
                    }
                    targetConsumer.accept((R) cacheMap.get(sourceValue));
                });
        return (T) this;
    }
}
