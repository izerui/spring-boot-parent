package com.yj2025.weixin.work;

import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@EnableConfigurationProperties(WorkWeixinProperties.class)
@Configuration
public class WorkWeixinAutoConfiguration {

    @Bean
    public WorkWeixinTenantRuntimeOperator tenantConfigGetter(StringRedisTemplate redisTemplate) {
        return new WorkWeixinTenantRuntimeOperator.Default(redisTemplate);
    }

    @Bean
    public WorkWeixinTenantConfigOperator tenantConfigOperator(StringRedisTemplate redisTemplate, WorkWeixinProperties properties) {
        return new WorkWeixinTenantConfigOperator.Default(redisTemplate, properties);
    }

    @Bean
    public WorkWeixinMultiConfigRuntimeStorage multiConfigStorage(WorkWeixinTenantConfigOperator configOperator,
                                                                  WorkWeixinTenantRuntimeOperator runtimeOperator,
                                                                  ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders) {
        return new WorkWeixinMultiConfigRuntimeStorage(configOperator, runtimeOperator, apacheHttpClientBuilders);
    }


    @Bean
    @ConditionalOnBean(WxCpConfigStorage.class)
    public WorkWeixinService wxCpService(WxCpConfigStorage wxCpConfigStorage, WorkWeixinProperties properties) {
        WorkWeixinService wxCpService = new WorkWeixinServiceImpl();
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

    @ThreadSafe
    public static class WorkWeixinServiceImpl extends WxCpServiceImpl implements WorkWeixinService {
        @Override
        public WorkWeixinService tenant(String tenantId) {
            WorkWeixinMultiConfigRuntimeStorage wxCpConfigStorage = (WorkWeixinMultiConfigRuntimeStorage) getWxCpConfigStorage();
            wxCpConfigStorage.tenant(tenantId);
            return this;
        }
    }

}
