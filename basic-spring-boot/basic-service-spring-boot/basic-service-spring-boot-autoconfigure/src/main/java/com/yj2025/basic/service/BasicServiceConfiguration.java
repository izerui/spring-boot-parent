package com.yj2025.basic.service;

import com.yj2025.basic.service.firewall.ManagerFirewallAspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicServiceConfiguration {

    @Bean
    public ManagerFirewallAspect managerFirewallAspect(ApplicationContext applicationContext) {
        return new ManagerFirewallAspect(applicationContext);
    }
}
