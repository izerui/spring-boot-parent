package com.yj2025.gateway.security;

import com.yj2025.gateway.GatewayProxyProperties;
import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import com.yj2025.gateway.filter.AdditionHeaderFilter;
import com.yj2025.gateway.filter.IgnoreUrlsRemoveAuthorizationHeaderFilter;
import com.yj2025.gateway.filter.MaintenanceWebFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;

@Configuration
@Import({ServerSecurityConfiguration.JwtTokenConfig.class, ServerSecurityConfiguration.OpaqueRedisTokenConfig.class, ServerSecurityConfiguration.OpaqueRestTokenConfig.class})
public class ServerSecurityConfiguration {

    @Autowired
    private GatewayProxyProperties properties;

    @Autowired
    private PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader;

    @Autowired
    private ObjectProvider<Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec>> authCustomizerObjectProvider;

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(Duration.ofHours(24));
        return corsConfiguration;
    }

    @Bean
    public ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter() {
        ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter = new ServerBearerTokenAuthenticationConverter();
        tokenAuthenticationConverter.setAllowUriQueryParameter(true);
        return tokenAuthenticationConverter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(c -> c.disable())
                .cors(s -> s.configurationSource(new UrlBasedCorsConfigurationSource(new PathPatternParser()) {{
                    this.registerCorsConfiguration("/**", buildConfig());
                }}))
                .requestCache().disable()
                .addFilterBefore(new MaintenanceWebFilter(properties), SecurityWebFiltersOrder.AUTHENTICATION) // 前置加入系统维护中过滤器
                .addFilterBefore(new IgnoreUrlsRemoveAuthorizationHeaderFilter(properties), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(new AdditionHeaderFilter(), SecurityWebFiltersOrder.AUTHORIZATION) // header 信息补充过滤器
                .oauth2ResourceServer(auth -> {
                    auth.bearerTokenConverter(tokenAuthenticationConverter())
                            .accessDeniedHandler(new AccessDeniedHandler())
                            .authenticationEntryPoint(new AuthenticationEntryPoint());
                    Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> specCustomizer = authCustomizerObjectProvider.getIfAvailable();
                    if (specCustomizer != null) {
                        specCustomizer.customize(auth);
                    }
                })
                .authorizeExchange(auth ->
                        auth.pathMatchers(properties.getIgnoredUrls()).permitAll()
                                .anyExchange()
                                .access(properties.getOauth2().getAuthType().getAuthorizationManager(pathMatcherAuthoritiesLoader))
                                .and()
                                .exceptionHandling()
                                .accessDeniedHandler(new AccessDeniedHandler())
                                .authenticationEntryPoint(new AuthenticationEntryPoint())
                );
        return http.build();
    }

    /**
     * redis token配置
     */
    @Configuration
    @ConditionalOnProperty(name = "gateway.oauth2.auth-type", matchIfMissing = true, havingValue = "OPAQUE_REDIS")
    public static class OpaqueRedisTokenConfig implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

        @Autowired
        private RedisConnectionFactory redisConnectionFactory;

        @Bean
        public RedisTokenStore redisTokenStore() {
            return new RedisTokenStore(redisConnectionFactory);
        }

        @Override
        public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
            auth.opaqueToken()
                    .introspector(new RedisTokenIntrospector(redisTokenStore()));
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "gateway.oauth2.auth-type", havingValue = "OPAQUE_REST")
    public static class OpaqueRestTokenConfig implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

        @Autowired
        private RedisConnectionFactory redisConnectionFactory;

        @Bean
        public RedisTokenStore redisTokenStore() {
            return new RedisTokenStore(redisConnectionFactory);
        }

        @Override
        public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
            auth.opaqueToken()
                    .introspector(new RedisTokenIntrospector(redisTokenStore()));
        }
    }

    /**
     * jwt token配置
     */
    @Configuration
    @ConditionalOnProperty(name = "gateway.oauth2.auth-type", havingValue = "JWT")
    public static class JwtTokenConfig implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

        @Override
        public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
            auth.jwt();
        }
    }

}
