package com.yj2025.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class JobApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(JobApplication.class, args);
        new CountDownLatch(1).await();
    }
}
