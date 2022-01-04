package com.yj2025.sms.event;


import com.yj2025.sms.SmsAuditRecord;
import com.yj2025.sms.SmsProperties;
import com.yj2025.sms.providers.SmsExecuteContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

public class SmsSpringEventListener implements ApplicationListener<SmsSpringEvent> {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${spring.application.name:''}")
    private String applicationName;
    @Autowired
    private SmsProperties properties;


    @Async
    @Override
    public void onApplicationEvent(SmsSpringEvent event) {
        SmsExecuteContext context = event.getContext();
        SmsAuditRecord record = new SmsAuditRecord();
        record.setAppName(applicationName);
        record.setSendTime(new Date());
        record.setTime(context.getTime());
        record.setSuccess(context.isSuccess());
        record.setRequest(context.getNativeRequest());
        record.setResponse(context.getNativeResponse());
        record.setError(context.getErrMsg());
        rabbitTemplate.convertAndSend(properties.getAudit().getExchange(), properties.getAudit().getRoutingKey(), record);
    }

}
