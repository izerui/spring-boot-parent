package com.yj2025.sample;

import com.google.common.util.concurrent.FutureCallback;
import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.time.Duration;

@Slf4j
public class ContextTest {

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
        Context.submitAsyncWait(5, 10, Duration.ofSeconds(10), new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("error: " + t.getMessage());
            }
        }, () -> "1", () -> "2");
    }
}
