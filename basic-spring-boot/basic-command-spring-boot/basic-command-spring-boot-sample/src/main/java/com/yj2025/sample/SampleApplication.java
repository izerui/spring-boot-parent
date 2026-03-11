package com.yj2025.sample;

import com.yj2025.jdbc.impl.PlatformJdbcRepositoryImpl;
import com.yj2025.jpa.impl.PlatformRepositoryImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = PlatformRepositoryImpl.class)
@EnableJdbcRepositories(repositoryBaseClass = PlatformJdbcRepositoryImpl.class)
@MapperScan("com.yj2025.sample.mapper")
public class SampleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
