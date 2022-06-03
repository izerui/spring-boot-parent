package com.yj2025.weixin.work.web;

import com.yj2025.weixin.work.provider.CpListener;
import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.provider.TpListener;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.config.TpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.tp.message.WxCpTpMessageRouter;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
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
@Import({CpController.class, TpController.class})
public class WxWebConfiguration {

    @Autowired
    private WxProperties properties;

    @Bean
    public WxCpMessageRouter cpMessageRouter(CpService cpService, ObjectProvider<CpListener> cpListeners) {
        WxCpMessageRouter router = new WxCpMessageRouter(cpService);
        router.rule()
                .interceptor((message, map, service, sessionManager) -> {
                    cpListeners.getIfAvailable(() -> CpListener.EMPTY)
                            .listener((String) map.get("tenantId"), message, (CpService) service);
                    return true;
                })
                .end();
        return router;
    }

    @Bean
    public WxCpTpMessageRouter tpMessageRouter(WxCpTpService tpService, ObjectProvider<TpListener> tpListeners) {
        WxCpTpMessageRouter router = new WxCpTpMessageRouter(tpService);
        router.rule()
                .interceptor((message, map, service, sessionManager) -> {
                    tpListeners.getIfAvailable(() -> TpListener.EMPTY)
                            .listener(message, (WxCpTpService) service);
                    return true;
                })
                .end();
        return router;
    }

    @Bean
    public TpConfig.JsSdkVerify jsSdkVerify() {
        return properties.getTpConfig().getJsSdkVerify();
    }


}
