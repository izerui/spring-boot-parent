package com.yj2025.metrics;

import org.apache.skywalking.apm.meter.micrometer.SkywalkingConfig;
import org.apache.skywalking.apm.meter.micrometer.SkywalkingMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MetricsConfiguration {

//    @ConditionalOnMissingBean
//    @Bean
//    public SkywalkingMeterRegistry skywalkingMeterRegistry() {
//        // Add rate configs If you need, otherwise using none args construct
//        SkywalkingConfig config = new SkywalkingConfig(Arrays.asList(""));
//        return new SkywalkingMeterRegistry(config);
//    }

}
