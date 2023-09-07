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
        // 使用master数据源
        dynamicDsService.testMaster("test222");
        // 使用sharding001数据源
        dynamicDsService.testMaster("sharding001");
        // 使用read数据源
        dynamicDsService.testRead("test222");
        // 使用sharding001数据源
        dynamicDsService.testRead("sharding001");
    }
}
