package com.yj2025.lock.support;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
public interface CompareRunnable {
    default void lessThan() throws Exception {
    }

    default void lessOrEqualThan() throws Exception {
    }

    default void equalThan() throws Exception {
    }

    default void greaterThan() throws Exception {
    }

    default void greaterOrEqualThan() throws Exception {
    }

}
