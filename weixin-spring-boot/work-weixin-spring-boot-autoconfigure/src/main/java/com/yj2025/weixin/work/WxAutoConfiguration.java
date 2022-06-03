package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.CpConfigOperator;
import com.yj2025.weixin.work.config.CpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.memory.MemoryCpConfigOperator;
import com.yj2025.weixin.work.config.redis.RedisCpConfigOperator;
import com.yj2025.weixin.work.impl.CpServiceImpl;
import com.yj2025.weixin.work.provider.CpConfigLoader;
import com.yj2025.weixin.work.web.WxWebConfiguration;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
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
@EnableConfigurationProperties(WxProperties.class)
@Configuration
@Import(WxWebConfiguration.class)
public class WxAutoConfiguration {

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
    public CpConfigStorageAdpatder cpConfigStorageAdpatder(CpConfigOperator tenantOperator,
                                                           WxProperties properties,
                                                           ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                                           ObjectProvider<CpConfigLoader> tenantWxCpConfigLoaders) {
        return new CpConfigStorageAdpatder(tenantOperator, properties, apacheHttpClientBuilders, tenantWxCpConfigLoaders);
    }


    @Bean
    public CpService wxCpService(CpConfigStorageAdpatder cpConfigStorageAdpatder, WxProperties properties) {
        CpService wxCpService = new CpServiceImpl();
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
    public WxCpTpService wxCpTpService(WxCpTpConfigStorage tpConfigStorage, WxProperties properties) {
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
        public MemoryCpConfigOperator memoryTenantOperator(WxProperties properties) {
            return new MemoryCpConfigOperator(properties);
        }

        @Bean
        public WxCpTpConfigStorage wxCpTpConfigStorage(WxProperties properties) {
            WxProperties.TpConfig tpConfig = properties.getTpConfig();
            WxCpTpDefaultConfigImpl config = new WxCpTpDefaultConfigImpl();
            config.setSuiteAccessTokenExpiresTime(tpConfig.getSuiteAccessTokenExpiresTime());
            config.setSuiteTicketExpiresTime(tpConfig.getSuiteTicketExpiresTime());
            config.setSuiteId(tpConfig.getSuiteId());
            config.setSuiteSecret(tpConfig.getSuiteSecret());
            config.setToken(tpConfig.getListenerToken());
            config.setAesKey(tpConfig.getListenerAesKey());
            config.setOauth2redirectUri(tpConfig.getOauth2redirectUri());
            config.setTmpDirFile(properties.getTmpDirFile());
            return config;
        }

    }

    @ConditionalOnProperty(value = "work.weixin.storage", havingValue = "redis")
    @Configuration
    public static class RedisOperator {


        @Bean
        public RedisCpConfigOperator redisTenantOperator(StringRedisTemplate redisTemplate,
                                                         WxProperties properties) {
            return new RedisCpConfigOperator(properties, redisTemplate);
        }

        @Bean
        public WxCpTpConfigStorage wxCpTpConfigStorage(StringRedisTemplate redisTemplate,
                                                       WxProperties properties) {
            WxProperties.TpConfig tpConfig = properties.getTpConfig();
            WxCpTpRedissonConfigImpl config = WxCpTpRedissonConfigImpl.builder()
                    .suiteId(tpConfig.getSuiteId())
                    .suiteSecret(tpConfig.getSuiteSecret())
                    .token(tpConfig.getListenerToken())
                    .aesKey(tpConfig.getListenerAesKey())
                    .corpId(tpConfig.getCorpId())
                    .corpSecret(tpConfig.getCorpSecret())
                    .providerSecret(tpConfig.getProviderSecret())
                    .wxRedisOps(new RedisTemplateWxRedisOps(redisTemplate)).build();
            return config;
        }

    }

}
