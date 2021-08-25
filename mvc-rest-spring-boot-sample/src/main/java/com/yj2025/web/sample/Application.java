package com.yj2025.web.sample;

import com.yj2025.rest.BusinessException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {


    @GetMapping(value = "/", produces = "application/json")
    public String getException() {
        throw new BusinessException("测试异常");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
