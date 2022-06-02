package com.yj2025.weixin.work;

import java.io.File;

import com.yj2025.weixin.work.config.TenantWxCpConfigOperator;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.WxTpConfig;
import com.yj2025.weixin.work.config.memory.MemoryTenantCpConfigOperator;
import com.yj2025.weixin.work.config.redis.RedisTenantCpConfigOperator;
import com.yj2025.weixin.work.impl.TenantWxCpServiceImpl;
import com.yj2025.weixin.work.listener.WeixinListenerConfiguration;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.RedissonWxRedisOps;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpTpRedissonConfigImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@EnableConfigurationProperties(WorkWeixinProperties.class)
@Configuration
@Import(WeixinListenerConfiguration.class)
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

    @Bean
    public WxCpTpService wxCpTpService(WxCpTpConfigStorage tpConfigStorage, WorkWeixinProperties properties) {
        WxCpTpService wxCpTpService = new WxCpTpServiceImpl();
        wxCpTpService.setWxCpTpConfigStorage(tpConfigStorage);
        int maxRetryTimes = properties.getMaxRetryTimes();
        if (maxRetryTimes < 0) {
            maxRetryTimes = 0;
        }
        int retrySleepMillis = properties.getRetrySleepMillis();
        if (retrySleepMillis < 0) {
            retrySleepMillis = 1000;
        }
        wxCpTpService.setRetrySleepMillis(retrySleepMillis);
        wxCpTpService.setMaxRetryTimes(maxRetryTimes);
        return wxCpTpService;
    }


    @ConditionalOnProperty(value = "work.weixin.storage", matchIfMissing = true, havingValue = "memory")
    @Configuration
    public static class MemoryOperator {


        @Bean
        public MemoryTenantCpConfigOperator memoryTenantOperator(WorkWeixinProperties properties) {
            return new MemoryTenantCpConfigOperator(properties);
        }

        @Bean
        public WxCpTpConfigStorage wxCpTpConfigStorage(WorkWeixinProperties properties) {
            WxTpConfig tpConfig = properties.getTpConfig();
            WxCpTpDefaultConfigImpl config = new WxCpTpDefaultConfigImpl();
            config.setSuiteAccessTokenExpiresTime(tpConfig.getSuiteAccessTokenExpiresTime());
            config.setSuiteTicketExpiresTime(tpConfig.getSuiteTicketExpiresTime());
            config.setSuiteId(tpConfig.getSuiteId());
            config.setSuiteSecret(tpConfig.getSuiteSecret());
            config.setToken(tpConfig.getToken());
            config.setAesKey(tpConfig.getAesKey());
            config.setOauth2redirectUri(tpConfig.getOauth2redirectUri());
            config.setTmpDirFile(properties.getTmpDirFile());
            return config;
        }

    }

    @ConditionalOnProperty(value = "work.weixin.storage", havingValue = "redis")
    @Configuration
    public static class RedisOperator {


        @Bean
        public RedisTenantCpConfigOperator redisTenantOperator(StringRedisTemplate redisTemplate,
                                                               WorkWeixinProperties properties) {
            return new RedisTenantCpConfigOperator(properties, redisTemplate);
        }

        @Bean
        public WxCpTpConfigStorage wxCpTpConfigStorage(StringRedisTemplate redisTemplate,
                                                       WorkWeixinProperties properties) {
            WxTpConfig tpConfig = properties.getTpConfig();
            WxCpTpRedissonConfigImpl config = WxCpTpRedissonConfigImpl.builder()
                    .suiteId(tpConfig.getSuiteId())
                    .suiteSecret(tpConfig.getSuiteSecret())
                    .token(tpConfig.getToken())
                    .aesKey(tpConfig.getAesKey())
                    .corpId(tpConfig.getCorpId())
                    .corpSecret(tpConfig.getCorpSecret())
                    .providerSecret(tpConfig.getProviderSecret())
                    .wxRedisOps(new RedisTemplateWxRedisOps(redisTemplate)).build();
            return config;
        }

    }

}
