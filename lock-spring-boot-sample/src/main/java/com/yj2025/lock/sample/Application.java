package com.yj2025.lock.sample;

import com.yj2025.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private Lock lock;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        lock.execute("temp-2021-08-25",() -> {
            System.out.println(System.currentTimeMillis());
            return null;
        });
    }
}
