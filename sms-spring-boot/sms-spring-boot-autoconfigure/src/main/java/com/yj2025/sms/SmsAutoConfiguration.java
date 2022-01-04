package com.yj2025.sms;

import com.yj2025.rabbit.RabbitAutoConfiguration;
import com.yj2025.sms.event.SmsSpringEventListener;
import com.yj2025.sms.providers.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@AutoConfigureAfter(RabbitAutoConfiguration.class)
@EnableConfigurationProperties({SmsProperties.class, MchuanSmsProperties.class, AlidayuSmsProperties.class})
public class SmsAutoConfiguration {

    @Order(1)
    @Bean
    @ConditionalOnProperty(value = "sms.type", havingValue = "alidayu")
    public SmsExecutor alidayuSmsSender() {
        return new AlidayuSmsExecutor();
    }

    @Order(0)
    @Bean
    @ConditionalOnProperty(value = "sms.type", matchIfMissing = true, havingValue = "mchuan")
    public SmsExecutor mchuanSmsSender() {
        return new MchuanSmsExecutor();
    }

    @Bean
    public SmsSender smsSender() {
        return new SmsSenderImpl();
    }

    @Bean
    public SmsSpringEventListener smsSpringEventListener() {
        return new SmsSpringEventListener();
    }

}
