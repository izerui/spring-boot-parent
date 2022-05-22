package com.yj2025.oauth2.jwt;

import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.security.DefaultPasswordEncoderMatchor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JwtServerSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtServerSampleApplication.class, args);
    }
}
