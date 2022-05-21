package com.yj2025.lock.support;

import java.util.Objects;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
@FunctionalInterface
public interface ThrowsFunction<T, R> {
    R apply(T t) throws Exception;

    default <V> ThrowsFunction<V, R> compose(ThrowsFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    default <V> ThrowsFunction<T, V> andThen(ThrowsFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    static <T> ThrowsFunction<T, T> identity() {
        return t -> t;
    }
}
