package com.yj2025.doc.sample;

import cn.langpy.kotime.config.DefaultConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application implements CommandLineRunner {

    @ApiOperation("测试doc")
    @GetMapping("/test")
    public String test(@RequestParam("name") String name) {
        return "hello " + name + "!!!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private DefaultConfig defaultConfig;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(serverProperties.getShutdown());
        System.out.println(defaultConfig.getClass());
    }
}
