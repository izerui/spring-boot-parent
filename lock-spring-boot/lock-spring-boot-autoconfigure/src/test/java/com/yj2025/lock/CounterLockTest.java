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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryForever(100));
//        new Thread(() -> curatorFramework.start()).start();
        curatorFramework.start();
        Thread.sleep(5000L);
        CounterLock counterLock = new CounterLock(curatorFramework);

        String path = UUID.randomUUID().toString();
        counterLock.initialize(path);

        AtomicBoolean finish = new AtomicBoolean(false);
        // 等待10秒
        counterLock.runWithAsyncUntil(path, 10, aLong -> aLong > 1000, predicateStatus -> {
            System.out.println("计数器的值为： " + predicateStatus.getCounterValue());
            if (predicateStatus.isSatisfy()) {
                System.out.println("=======================够了1000");
                System.out.println("=======================够了1000");
                System.out.println("=======================够了1000");
            } else {
                System.out.println("=======================等了这么久，还是不到1000，算了退出");
                System.out.println("=======================等了这么久，还是不到1000，算了退出");
                System.out.println("=======================等了这么久，还是不到1000，算了退出");
            }
            countDownLatch.countDown();
            finish.set(true);
        });


        Set<Long> sets = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                while (!finish.get()) {
                    AtomicValue<Long> increment = counterLock.increment(path);
                    if (sets.add(increment.postValue())) {
                        System.out.println(Thread.currentThread().getName() + " value: " + increment.postValue());
                    } else {
                        throw new RuntimeException("递增冲突，发现重复的数字：" + increment.postValue());
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
        countDownLatch.await();

    }
}
