package com.yj2025.doc.sample;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    @ApiOperation("测试doc")
    @GetMapping("/test")
    public String test(@RequestParam("name") String name) {
        return "hello " + name + "!!!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
