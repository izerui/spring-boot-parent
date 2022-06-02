package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.TenantWxCpConfigOperator;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.memory.MemoryTenantCpConfigOperator;
import com.yj2025.weixin.work.config.redis.RedisTenantCpConfigOperator;
import com.yj2025.weixin.work.impl.TenantWxCpServiceImpl;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@EnableConfigurationProperties(WorkWeixinProperties.class)
@Configuration
public class WorkWeixinAutoConfiguration {

    /**
     * 企业微信自建应用配置适配器
     *
     * @param tenantOperator
     * @param properties
     * @param apacheHttpClientBuilders
     * @param tenantWxCpConfigLoaders
     * @return
     */
    @Bean
    public TenantWxCpConfigStorageAdpatder cpConfigStorageAdpatder(TenantWxCpConfigOperator tenantOperator,
                                                                   WorkWeixinProperties properties,
                                                                   ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                                                   ObjectProvider<TenantWxCpConfigLoader> tenantWxCpConfigLoaders) {
        return new TenantWxCpConfigStorageAdpatder(tenantOperator, properties, apacheHttpClientBuilders, tenantWxCpConfigLoaders);
    }


    @Bean
    public TenantWxCpService wxCpService(TenantWxCpConfigStorageAdpatder cpConfigStorageAdpatder, WorkWeixinProperties properties) {
        TenantWxCpService wxCpService = new TenantWxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(cpConfigStorageAdpatder);
        int maxRetryTimes = properties.getMaxRetryTimes();
        if (maxRetryTimes < 0) {
            maxRetryTimes = 0;
        }
        int retrySleepMillis = properties.getRetrySleepMillis();
        if (retrySleepMillis < 0) {
            retrySleepMillis = 1000;
        }
        wxCpService.setRetrySleepMillis(retrySleepMillis);
        wxCpService.setMaxRetryTimes(maxRetryTimes);
        return wxCpService;
    }


    @ConditionalOnProperty(value = "work.weixin.storage", matchIfMissing = true, havingValue = "memory")
    @Configuration
    public class MemoryOperator {


        @Bean
        public MemoryTenantCpConfigOperator memoryTenantOperator(ObjectProvider<TenantWxCpConfigLoader> wxCpConfigLoaders, WorkWeixinProperties properties) {
            return new MemoryTenantCpConfigOperator(properties);
        }

    }

    @ConditionalOnProperty(value = "work.weixin.storage", havingValue = "redis")
    @Configuration
    public class RedisOperator {


        @Bean
        public RedisTenantCpConfigOperator redisTenantOperator(StringRedisTemplate redisTemplate,
                                                               WorkWeixinProperties properties) {
            return new RedisTenantCpConfigOperator(properties, redisTemplate);
        }

    }

}
