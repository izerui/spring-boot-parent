package com.yj2025.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DynamicDsApplication implements CommandLineRunner {

    @Autowired
    private DynamicDsService dynamicDsService;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dynamicDsService.testMaster("test222");
        dynamicDsService.testMaster("sharding001");
        dynamicDsService.testRead("test222");
        dynamicDsService.testRead("sharding001");
    }
}
