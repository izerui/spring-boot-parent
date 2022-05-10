package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.security.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


@Configuration
@RestController
public class TokenController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    private BearerTokenResolver bearerTokenResolver;

    @PostConstruct
    public void init() {
        // 允许access_token 以url参数传递获取
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        defaultBearerTokenResolver.setAllowUriQueryParameter(true);
        this.bearerTokenResolver = defaultBearerTokenResolver;
    }

    @GetMapping("/oauth/revoke")
    public RespVo revokeToken(HttpServletRequest request) {
        String accessToken = bearerTokenResolver.resolve(request);
        if (accessToken != null) {
            boolean revokeToken = consumerTokenServices.revokeToken(accessToken);
            if (revokeToken) {
                return RespVo.success("登出成功!");
            }
        }
        return RespVo.error("logout_error", "token注销失败");
    }

    @GetMapping("/userinfo")
    public RespVo userInfo(Principal principal) {
        return RespVo.success(principal);
    }
}
