package com.yj2025.oauth2.server;

import com.yj2025.oauth2.server.advice.GlobalResponseBodyAdviceAdapter;
import com.yj2025.oauth2.server.controller.TokenController;
import com.yj2025.oauth2.server.security.AuthorizationServerConfiguration;
import com.yj2025.oauth2.server.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@Import({SecurityConfiguration.class, AuthorizationServerConfiguration.class, TokenController.class})
public class Oauth2ServerConfiguration {

    private final static String[] ignoreWrapPathMatchers = {
            "/oauth/check_token", // opaque token，通过rest请求方式进行token校验地址
            "/rsa/key" // jwt获取公钥用来校验token的地址
    };

    @Bean
    public GlobalResponseBodyAdviceAdapter globalResponseBodyAdviceAdapter(ErrorAttributes errorAttributes) {
        return new GlobalResponseBodyAdviceAdapter(errorAttributes, ignoreWrapPathMatchers);
    }
}
