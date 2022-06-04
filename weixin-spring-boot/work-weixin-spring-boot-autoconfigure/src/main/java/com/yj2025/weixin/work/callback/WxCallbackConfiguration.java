package com.yj2025.weixin.work.callback;

import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpService;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.provider.CpListener;
import com.yj2025.weixin.work.provider.TpListener;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.tp.message.WxCpTpMessageRouter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "work.weixin.listener-enabled", havingValue = "true")
@Import({CpCallbackController.class, TpCallbackController.class})
public class WxCallbackConfiguration {

    @Autowired
    private WxProperties properties;

    @Bean
    public WxCpMessageRouter cpMessageRouter(CpService cpService, ObjectProvider<CpListener> cpListeners) {
        WxCpMessageRouter router = new WxCpMessageRouter(cpService);
        router.rule()
                .interceptor((message, map, service, sessionManager) -> {
                    cpListeners.forEach(cpListener -> {
                        cpListener.listener((String) map.get("tenantId"), message, (CpService) service);
                    });
                    return true;
                })
                .end();
        return router;
    }

    @Bean
    public WxCpTpMessageRouter tpMessageRouter(TpService tpService, ObjectProvider<TpListener> tpListeners) {
        WxCpTpMessageRouter router = new WxCpTpMessageRouter(tpService);
        router.rule()
                .interceptor((message, map, service, sessionManager) -> {
                    tpListeners.forEach(tpListener -> {
                        tpListener.listener(message, tpService);
                    });
                    return true;
                })
                .end();
        return router;
    }

    @Bean
    public WxProperties.TpConfig.JsSdkVerify jsSdkVerify() {
        return properties.getTpConfig().getJsSdkVerify();
    }

    @Bean
    public CacheAuthBindingListener cacheAuthBindingListener(CpService cpService) {
        return new CacheAuthBindingListener(cpService);
    }

}
