package com.yj2025.lock.support;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
@FunctionalInterface
public interface ThrowsRunnable {
    void run() throws Exception;
}
