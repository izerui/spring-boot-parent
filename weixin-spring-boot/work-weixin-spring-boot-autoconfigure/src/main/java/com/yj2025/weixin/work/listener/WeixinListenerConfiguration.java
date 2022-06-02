package com.yj2025.weixin.work.listener;

import com.yj2025.weixin.work.TenantWxCpService;
import com.yj2025.weixin.work.WorkWeixinCpListener;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "work.weixin.listener-enabled", havingValue = "true")
@Import(WxCpListenerController.class)
public class WeixinListenerConfiguration {


    @Bean
    public WxCpMessageRouter wxCpMessageRouter(TenantWxCpService tenantWxCpService, ObjectProvider<WorkWeixinCpListener> listenerObjectProvider) {
        WxCpMessageRouter wxCpMessageRouter = new WxCpMessageRouter(tenantWxCpService);
        wxCpMessageRouter.rule()
                .interceptor((message, map, service, wxSessionManager) -> {
                    listenerObjectProvider.getIfAvailable(() -> WorkWeixinCpListener.EMPTY)
                            .listener((String) map.get("tenantId"), message, (TenantWxCpService) service);
                    return true;
                })
                .end();
        return wxCpMessageRouter;
    }


}
