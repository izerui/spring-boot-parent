package com.yj2025.performance;

/**
 * @author liuyuhua
 * @date 2022/5/25
 */
@FunctionalInterface
public interface Customizer<T> {

    /**
     * 对输入参数自定义
     *
     * @param t 输入参数
     */
    void customize(T t);

    /**
     * 返回未改变的输入参数
     */
    static <T> Customizer<T> withDefaults() {
        return t -> {};
    }
}
