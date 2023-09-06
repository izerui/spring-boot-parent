package com.yj2025.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TenantApplication implements CommandLineRunner {

    @Autowired
    private TenantService tenantService;

    public static void main(String[] args) {
        SpringApplication.run(TenantApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        tenantService.testTenant("测试tenant001");
    }
}
