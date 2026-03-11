package com.yj2025.performance.delay;


import org.junit.jupiter.api.Test;

/**
 * @author liuyuhua
 * @date 2022/5/24
 */
public class DelayTest {

    @Test
    public void testDelay() throws Exception {
        // 使用Spring可不用调用afterPropertiesSet和destroy方法
        DelayedTaskExecutor delayedTaskExecutor = new DelayedTaskExecutor("cache-delay");
        delayedTaskExecutor.afterPropertiesSet();
        delayedTaskExecutor.submit(() -> System.out.println("hello"), 2000);
        delayedTaskExecutor.destroy();
    }
}
