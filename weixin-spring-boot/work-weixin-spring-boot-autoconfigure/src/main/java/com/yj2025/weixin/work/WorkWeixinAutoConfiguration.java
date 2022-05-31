package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.adpatder.TenantWxCpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageOperator;
import com.yj2025.weixin.work.config.impl.memory.MemoryTenantOperator;
import com.yj2025.weixin.work.config.impl.redis.RedisTenantOperator;
import com.yj2025.weixin.work.impl.TenantWxCpServiceImpl;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    @Bean
    public TenantWxCpConfigStorageAdpatder tenantWxCpConfigStorageAdpatder(TenantWxCpConfigStorageOperator tenantOperator,
                                                                           WorkWeixinProperties properties,
                                                                           ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders) {
        return new TenantWxCpConfigStorageAdpatder(tenantOperator, properties, apacheHttpClientBuilders);
    }


    @Bean
    @ConditionalOnBean(WxCpConfigStorage.class)
    public TenantWxCpService wxCpService(WxCpConfigStorage wxCpConfigStorage, WorkWeixinProperties properties) {
        TenantWxCpService wxCpService = new TenantWxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(wxCpConfigStorage);
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
        public MemoryTenantOperator memoryTenantOperator(WorkWeixinProperties properties) {
            return new MemoryTenantOperator(properties);
        }

    }

    @ConditionalOnProperty(value = "work.weixin.storage", havingValue = "redis")
    @Configuration
    public class RedisOperator {


        @Bean
        public RedisTenantOperator redisTenantOperator(StringRedisTemplate redisTemplate,
                                                       WorkWeixinProperties properties) {
            return new RedisTenantOperator(properties, redisTemplate);
        }

    }

}
