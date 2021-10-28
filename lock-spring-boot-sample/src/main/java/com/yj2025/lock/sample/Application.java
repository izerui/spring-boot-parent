package com.yj2025.lock.sample;

import com.google.common.collect.Lists;
import com.yj2025.commons.util.ThreadPoolExecutors;
import com.yj2025.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private Lock lock;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        difThreads();
        sameParentThreads();
        new CountDownLatch(1).await();
    }

    private void difThreads() throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    lock.execute("temp-2021-08-25", () -> {
                        log.info(count.get() + "");
                        Assert.state(count.get() == 0, "锁失败，值冲突");
                        count.set(1);
//                        Thread.sleep(new Random().nextInt(3) * 1000);
                        count.set(0);
                        return null;
                    });
                }
            }).start();
        }

    }

    private void sameParentThreads() throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            integers.add(i);
        }

        ThreadPoolExecutors.newInstance().execute(integers.stream().map(integer -> {
            Runnable runnable = () -> {
                while (true) {
                    lock.execute("temp-2021-08-25", () -> {
                        log.info(count.get() + "");
                        Assert.state(count.get() == 0, "锁失败，值冲突");
                        count.set(1);
                        Thread.sleep(new Random().nextInt(3) * 100);
                        count.set(0);
                        return null;
                    });
                }
            };
            return runnable;
        }).collect(Collectors.toList()), 5, 10);
    }

}
