package com.yj2025.performance;

import java.util.Objects;

@FunctionalInterface
public interface ThrowsConsumer<T> {

    void accept(T t) throws Exception;

    default ThrowsConsumer<T> andThen(ThrowsConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
