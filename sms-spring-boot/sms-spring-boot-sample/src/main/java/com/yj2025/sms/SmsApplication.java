package com.yj2025.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@SpringBootApplication
public class SmsApplication implements CommandLineRunner {

    @Autowired
    private SmsSender smsSender;

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String phone = "18100279963";
        String template = "您好，${code}为您的注册验证码，请在10分钟内输入以完成验证。";
        AtomicReference<String> reference = new AtomicReference<>();
        smsSender.sendCaptcha(template, s -> new HashMap() {{
            reference.set(s);
            put("code", s);
        }}, "biz001", 20, phone);

        boolean validCaptcha = smsSender.isValidCaptcha("biz001", reference.get(), phone);
        log.info("验证结果: " + validCaptcha);
    }
}
