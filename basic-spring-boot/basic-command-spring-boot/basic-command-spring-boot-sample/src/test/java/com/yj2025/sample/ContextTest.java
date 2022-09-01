package com.yj2025.sample;

import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

@Slf4j
public class ContextTest {

    @Test
    public void test001() {
        List<String> alist = null;
        List<Integer> blist = null;
        Context.matchAndBundleList(alist, blist, (s, integer) -> s.equals(integer.toString()),(s, integer) -> {
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
}
