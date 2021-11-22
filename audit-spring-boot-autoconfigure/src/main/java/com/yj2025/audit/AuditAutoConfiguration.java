package com.yj2025.audit;

import io.swagger.annotations.ApiOperation;
import org.apache.skywalking.apm.meter.micrometer.SkywalkingConfig;
import org.apache.skywalking.apm.meter.micrometer.SkywalkingMeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;

/**
 * Created by serv on 2016/12/8.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableConfigurationProperties(AuditProperties.class)
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "audit.type", matchIfMissing = true, havingValue = "rabbit")
    public AuditContext auditContext(RabbitTemplate rabbitTemplate, AuditProperties auditProperties) {
        RabbitAuditProperties rabbitAuditProperties = auditProperties.getRabbit();
        if (rabbitAuditProperties == null) {
            rabbitAuditProperties = new RabbitAuditProperties();
        }
        return new RabbitAuditContextImpl(rabbitTemplate, rabbitAuditProperties);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnClass(ApiOperation.class)
    public AuditWebMethodAspect auditWebMethodAspect(AuditContext auditContext,
                                                     @Value("${spring.application.name:null}") String application) {
        return new AuditWebMethodAspect(auditContext, application);
    }

    @Bean
    public SkywalkingMeterRegistry skywalkingMeterRegistry() {
        // Add rate configs If you need, otherwise using none args construct
        SkywalkingConfig config = new SkywalkingConfig(Arrays.asList(""));
        return new SkywalkingMeterRegistry(config);
    }

}
