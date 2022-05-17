package com.yj2025.oauth2.server.security.opaque;


import com.yj2025.oauth2.server.security.ExpandEndpointsConfigurer;
import com.yj2025.oauth2.server.security.TokenInfoEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;


@Configuration
@ConditionalOnProperty(name = "oauth2.server.jwt.enabled", matchIfMissing = true, havingValue = "false")
public class OpaqueTokenConfiguration implements ExpandEndpointsConfigurer {

    @Autowired
    private TokenInfoEnhancer tokenInfoEnhancer;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 通过 /oauth/check_token 检查token，同时返回增强的信息， 当使用url验证token的时候可以返回增强内容
        endpoints.tokenEnhancer(tokenInfoEnhancer); //配置Opaque的内容增强器
    }
}