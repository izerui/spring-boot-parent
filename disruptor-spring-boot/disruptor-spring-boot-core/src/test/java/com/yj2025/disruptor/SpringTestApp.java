package com.yj2025.disruptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringTestApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringTestApp.class, args);
    }
}
