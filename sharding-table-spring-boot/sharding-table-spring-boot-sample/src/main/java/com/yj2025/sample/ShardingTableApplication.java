package com.yj2025.sample;

import com.yj2025.sharding.ShardingTableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class ShardingTableApplication implements CommandLineRunner {

    @Autowired
    private ShardingTableContext shardingTableContext;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(ShardingTableApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        String tableName = shardingTableContext.getTableName(dataSource, "a", "fff");
        System.out.println("target: "+ tableName);
    }
}
