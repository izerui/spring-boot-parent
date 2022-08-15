package com.yj2025.gateway.proxy.loadbalancer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * 覆盖 {@link org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration} 中的默认bean。
 */
public class LoadBalanceConfig {

    @ConditionalOnProperty(prefix = "gateway", name = "develop", havingValue = "true")
    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new DevelopLoadBalancer(loadBalancerClientFactory.getLazyProvider(name,
                ServiceInstanceListSupplier.class), name);
    }

}
