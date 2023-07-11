package com.yj2025.sample;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.List;

@SpringBootApplication
@EnableJdbcRepositories
public class ShardingTableApplication implements CommandLineRunner {

    @Autowired
    private SampleService sampleService;

    public static void main(String[] args) {
        SpringApplication.run(ShardingTableApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        List<TestUser> list = sampleService.findList("copy1");
        System.out.println("list: " + list.size());

        List<TestUser> copy1 = sampleService.findList1("copy1");
        System.out.println("list1: " + list.size());
    }
}
