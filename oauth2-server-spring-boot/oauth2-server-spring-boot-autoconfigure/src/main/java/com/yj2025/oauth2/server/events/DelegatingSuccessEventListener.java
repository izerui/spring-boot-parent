package com.yj2025.oauth2.server.events;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.LoginSuccessHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 登录成功回调事件代理器
 */
public class DelegatingSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final List<LoginSuccessHandler> delegates;

    public DelegatingSuccessEventListener(Iterator<LoginSuccessHandler> iterator) {
        delegates = new ArrayList<>();
        iterator.forEachRemaining(delegates::add);
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        for (LoginSuccessHandler delegate : this.delegates) {
            Authentication authentication = authenticationSuccessEvent.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                delegate.onAuthenticationSuccess((User) authentication.getPrincipal());
            }
        }
    }
}
