package com.yj2025.oauth2.server.controller;

import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.security.support.RespVo;
import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.LogoutSuccessHandler;
import com.yj2025.oauth2.server.security.TokenSerivces;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;


@Configuration
@RestController
public class TokenController {

    private BearerTokenResolver bearerTokenResolver;
    @Autowired
    private TokenSerivces tokenSerivces;

    @Autowired
    private ObjectProvider<LogoutSuccessHandler> logoutSuccessHandlerObjectProvider;

    @PostConstruct
    public void init() {
        // 允许access_token 以url参数传递获取
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        defaultBearerTokenResolver.setAllowUriQueryParameter(true);
        this.bearerTokenResolver = defaultBearerTokenResolver;
    }

    @GetMapping(MappingUrls.OAUTH_REVOKE_URL)
    public RespVo revokeToken(HttpServletRequest request) {
        String accessToken = bearerTokenResolver.resolve(request);
        OAuth2Authentication authentication = tokenSerivces.loadAuthentication(accessToken);
        boolean revokeToken = tokenSerivces.revokeToken(accessToken);
        if (revokeToken) {
            logoutSuccessHandlerObjectProvider.ifAvailable(logoutSuccessHandler -> {
                logoutSuccessHandler.revokeTokenSuccess((User) authentication.getPrincipal());
            });
            return RespVo.success("登出成功!");
        }
        return RespVo.error("logout_error", "token注销失败");
    }

}
