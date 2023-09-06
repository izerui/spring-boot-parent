package com.yj2025.dynamic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicAutoConfiguration {

    @Bean
    public TenantDatasourceProvider tenantDatasourceProvider() {
        return new TenantDatasourceProvider();
    }

}
