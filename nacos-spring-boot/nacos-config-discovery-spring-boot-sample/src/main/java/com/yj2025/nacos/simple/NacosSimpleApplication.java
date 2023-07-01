package com.yj2025.nacos.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class NacosSimpleApplication implements CommandLineRunner {

    @Value("${aaa}")
    private String aaa;

    public static void main(String[] args) {
        SpringApplication.run(NacosSimpleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(aaa);
    }
}
