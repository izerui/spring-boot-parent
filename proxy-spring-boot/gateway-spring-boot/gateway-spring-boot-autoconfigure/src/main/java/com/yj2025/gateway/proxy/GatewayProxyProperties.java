package com.yj2025.gateway.proxy;

import com.nimbusds.jose.util.ArrayUtils;
import com.yj2025.gateway.proxy.security.jwt.JwtAuthorizationManager;
import com.yj2025.gateway.proxy.security.rest.OpaqueRestAuthorizationManager;
import com.yj2025.oauth2.security.support.MappingUrls;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import java.util.Optional;

@Data
@ConfigurationProperties(prefix = "gateway")
public class GatewayProxyProperties {

    private boolean develop;
    private boolean maintenance;
    private String whitelistIp;
    private String[] ignoredUrls;
    private Oauth2Properties oauth2 = new Oauth2Properties();

    public String[] getIgnoredUrls() {
        return ArrayUtils.concat(MappingUrls.GATEWAY_IGNORE_URLS, Optional.ofNullable(ignoredUrls).orElse(new String[0]));
    }

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
        OPAQUE_REST {
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
