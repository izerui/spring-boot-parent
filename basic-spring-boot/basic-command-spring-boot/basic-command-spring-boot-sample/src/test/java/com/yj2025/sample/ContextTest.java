package com.yj2025.sample;

import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ContextTest {

    @Test
    public void test001() {
        List<String> alist = null;
        List<Integer> blist = null;
        Context.matchAndBundleFirst(alist, blist, (s, integer) -> s.equals(integer.toString()), (s, integer) -> {
            s = integer.toString() + "1";
        });
    }

    @Test
    public void test() {
        Context.submitAsync(5, 10, () -> {
            log.info("1");
        }, () -> {
            log.info("2");
        });
        log.info("3");
    }

    @Test
    public void testAwait() {
        while (true) {
            Context.submitAsyncWait(5, 10, Duration.ofSeconds(10), () -> {
                log.info("1");
            }, () -> {
                log.info("2");
                Context.tryWith(() -> Thread.sleep(RandomUtils.nextLong(1000, 5000)));
            });
            log.info("3");
            System.out.println("---------------");
//            Context.tryWith(() -> Thread.sleep(3000));
        }

    }

    @Test
    public void testAwait2() {
        List<String> strings = Context.submitAsyncWaitReturn(5, 10, Duration.ofSeconds(60), () -> "1", () -> "2");
        System.out.println(strings);
    }

    @Test
    public void testDelay() throws InterruptedException {
        Context.runDelayed("延迟任务", currentCount -> {
            log.info("{}", currentCount);
            // 运行到第三次后，就返回false，停止运行
            if (currentCount == 3) {
                return false;
            }
            return true;
        }, 5, 3, 10, true);
    }

}
