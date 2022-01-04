package com.yj2025.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    private static final String SMS_RECORD_EXCHANGE = "platform";
    private static final String SMS_RECORD_ROUTING_KEY = "platform.mchuan.sms.record";


    private String type;
    private String signName = "我的经管";

    private AuditProperties audit = new AuditProperties();


    @Data
    public static class AuditProperties {
        private String exchange = SMS_RECORD_EXCHANGE;
        private String routingKey = SMS_RECORD_ROUTING_KEY;
    }
}
