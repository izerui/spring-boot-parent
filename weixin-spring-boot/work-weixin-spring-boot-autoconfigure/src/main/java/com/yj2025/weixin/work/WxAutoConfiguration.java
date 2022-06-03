package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.ConfigOperator;
import com.yj2025.weixin.work.config.memory.MemoryConfigOperator;
import com.yj2025.weixin.work.config.redis.RedisConfigOperator;
import com.yj2025.weixin.work.impl.ConfigStorageAdpatderImpl;
import com.yj2025.weixin.work.impl.CpServiceImpl;
import com.yj2025.weixin.work.impl.WxErrorHandler;
import com.yj2025.weixin.work.provider.CpConfigLoader;
import com.yj2025.weixin.work.provider.TpAuthConfigLoader;
import com.yj2025.weixin.work.web.WxWebConfiguration;
import lombok.Builder;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpTpRedissonConfigImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Proxy;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@EnableConfigurationProperties(WxProperties.class)
@Configuration
@Import(WxWebConfiguration.class)
public class WxAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 企业微信自建应用配置适配器
     *
     * @param tenantOperator
     * @param properties
     * @param apacheHttpClientBuilders
     * @param cpConfigLoaders
     * @param authConfigLoaders
     * @return
     */
    @Bean
    public ConfigStorageAdpatder cpConfigStorageAdpatder(ConfigOperator tenantOperator,
                                                         WxProperties properties,
                                                         ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                                         ObjectProvider<CpConfigLoader> cpConfigLoaders,
                                                         ObjectProvider<TpAuthConfigLoader> authConfigLoaders) {
        return new ConfigStorageAdpatderImpl(tenantOperator, properties, apacheHttpClientBuilders, cpConfigLoaders, authConfigLoaders);
    }


    @Bean
    public CpService wxCpService(WxCpConfigStorage cpConfigStorageAdpatder,
                                 WxCpTpService tpService,
                                 WxProperties properties) {
        CpServiceImpl wxCpService = new CpServiceImpl();
        wxCpService.setTpService(tpService);
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
        return (CpService) Proxy.newProxyInstance(wxCpService.getClass().getClassLoader(),
                wxCpService.getClass().getInterfaces(),
                new WxErrorHandler(wxCpService, applicationContext));
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
        return (WxCpTpService) Proxy.newProxyInstance(wxCpTpService.getClass().getClassLoader(),
                new Class[]{WxCpTpService.class},
                new WxErrorHandler(wxCpTpService, applicationContext));
    }


    @ConditionalOnProperty(value = "work.weixin.storage", matchIfMissing = true, havingValue = "memory")
    @Configuration
    public static class MemoryOperator {


        @Bean
        public MemoryConfigOperator memoryTenantOperator(WxProperties properties) {
            return new MemoryConfigOperator(properties);
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
        public RedisConfigOperator redisTenantOperator(StringRedisTemplate redisTemplate,
                                                       WxProperties properties) {
            return new RedisConfigOperator(properties, redisTemplate);
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
                    .keyPrefix("work:weixin-tp")
                    .wxRedisOps(new RedisTemplateWxRedisOps(redisTemplate)).build();
            return config;
        }


    }

}
