package com.yj2025.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.retry.RetryForever;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
public class CounterLockTest {
    public static void main(String[] args) throws InterruptedException {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryForever(100));
//        new Thread(() -> curatorFramework.start()).start();
        curatorFramework.start();
        Thread.sleep(5000L);
        CounterLock counterLock = new CounterLock(curatorFramework);

        String path = UUID.randomUUID().toString();
        counterLock.initialize(path);

        AtomicBoolean finish = new AtomicBoolean(false);
        Set<Long> sets = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (!finish.get()) {
                    AtomicValue<Long> increment = counterLock.increment(path);
                    if (sets.add(increment.postValue())) {
                        System.out.println(Thread.currentThread().getName() + " value: " + increment.postValue());
                    } else {
                        throw new RuntimeException("递增冲突，发现重复的数字：" + increment.postValue());
                    }
                }
            }).start();
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            while (!finish.get()) {
                counterLock.executeGreaterOrEqualThan(path,1000L, () -> {
                    System.out.println("=======================够了1000");
                    System.out.println("=======================够了1000");
                    System.out.println("=======================够了1000");
                    countDownLatch.countDown();
                    finish.set(true);
                });
            }
        }).start();
        countDownLatch.await();

    }
}
