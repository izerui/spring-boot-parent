package com.yj2025.gateway.proxy;

import com.yj2025.gateway.proxy.security.jwt.JwtAuthorizationManager;
import com.yj2025.gateway.proxy.security.rest.OpaqueRestAuthorizationManager;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;

@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayProxyProperties {

    private boolean maintenance;
    private String whitelistIp;
    private String[] ignoredUrls;
    private Oauth2Properties oauth2 = new Oauth2Properties();

    @Data
    public static class Oauth2Properties {

        private String appName;
        private String clientId;
        private String clientSecret;
        private AuthenticationType authType = AuthenticationType.OPAQUE_REDIS;
    }

    public enum AuthenticationType {
        OPAQUE_REDIS {
            @Override
            public ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
                return new OpaqueRestAuthorizationManager(pathMatcherAuthoritiesLoader);
            }
        },
        OPAQUE_REST{
            @Override
            public ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
                return new OpaqueRestAuthorizationManager(pathMatcherAuthoritiesLoader);
            }
        },
        JWT {
            @Override
            public ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
                return new JwtAuthorizationManager(pathMatcherAuthoritiesLoader);
            }
        };

        public abstract ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader);
    }
}
