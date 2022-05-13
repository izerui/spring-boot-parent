package com.yj2025.oauth2.opaque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OpaqueServerSampleApplication {

    @Bean
    public MD5PasswordEncoderMatchor passwordEncoderMatchor() {
        return new MD5PasswordEncoderMatchor();
    }

    public static void main(String[] args) {
        SpringApplication.run(OpaqueServerSampleApplication.class, args);
    }
}
