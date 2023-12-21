package com.yj2025.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class MailConfiguration {

    @Bean
    public MailService mailService(JavaMailSender javaMailSender,
                                   @Value("${spring.mail.nickname:我的经管}") String fromNickName,
                                   MailProperties properties){
        return new MailServiceImpl(javaMailSender,fromNickName,properties);
    }
}
