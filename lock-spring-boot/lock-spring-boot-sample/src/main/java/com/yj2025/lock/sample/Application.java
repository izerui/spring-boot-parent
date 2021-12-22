package com.yj2025.lock.sample;

import com.yj2025.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
        difThreads();
        new CountDownLatch(1).await();
    }

    private void difThreads() {
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

}
