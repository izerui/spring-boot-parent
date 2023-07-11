package com.yj2025.sharding;

import com.yj2025.sharding.tenant.TenantMethodAspect;
import com.yj2025.sharding.tenant.TenantSharding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingAutoConfiguration {

    private final ApplicationContext applicationContext;

    public ShardingAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Bean
    public TenantMethodAspect repositoryQueryAspect() {
        return new TenantMethodAspect(applicationContext);
    }

    @Bean
    public TenantSharding tenantSharding() {
        return new TenantSharding(applicationContext);
    }
}
