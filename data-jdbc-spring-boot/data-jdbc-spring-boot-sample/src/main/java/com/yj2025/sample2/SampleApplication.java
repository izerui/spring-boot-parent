package com.yj2025.sample2;

import com.yj2025.basic.support.DbContext;
import com.yj2025.jdbc.impl.PlatformJdbcRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJdbcRepositories(repositoryBaseClass = PlatformJdbcRepositoryImpl.class)
public class SampleApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(DbContext.getDatabase(dataSource));
    }
}
