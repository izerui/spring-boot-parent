package com.ecworking.audit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    /**
     * 默认使用rabbit,后续考虑支持kafka
     */
    private String type = "rabbit";

    @NestedConfigurationProperty
    private RabbitAuditProperties rabbit;
}
