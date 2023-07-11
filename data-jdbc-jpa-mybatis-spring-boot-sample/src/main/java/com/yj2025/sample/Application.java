package com.yj2025.sample;

import com.yj2025.jpa.impl.PlatformRepositoryImpl;
import com.yj2025.sample.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = PlatformRepositoryImpl.class)
@EnableJdbcRepositories
@MapperScan("com.yj2025.sample.mybatis.mapper")
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("jpa插入到test_user: " + userService.addByJpa(300));
        log.info("jpa查询test_user: " + userService.findListByJpa("ent001").size());

        log.info("jdbc插入到test_user_ent001: " + userService.addByJdbc("ent001", 500));
        log.info("jdbc查询test_user_ent001: " + userService.findListByJdbc("ent001", "100").size());

        log.info("mybatis插入到test_user: " + userService.addByMybatis(100));
        log.info("mybatis查询test_user: " + userService.findPageByMybatis("ent001").getTotalElements());
    }
}
