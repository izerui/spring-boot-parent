package com.yj2025.gateway.proxy;

import com.yj2025.gateway.proxy.security.jwt.JwtAuthorizationManager;
import com.yj2025.gateway.proxy.security.rest.OpaqueRestAuthorizationManager;
import com.yj2025.oauth2.security.support.MappingUrls;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayProxyProperties {

    private boolean maintenance;
    private String whitelistIp;
    private String[] ignoredUrls;
    private Oauth2Properties oauth2 = new Oauth2Properties();

    public String[] getIgnoredUrls() {
        List<String> strings = Arrays.asList(MappingUrls.GATEWAY_IGNORE_URLS);
        for (String ignoredUrl : ignoredUrls) {
            strings.add(ignoredUrl);
        }
        return strings.toArray(new String[strings.size()]);
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
