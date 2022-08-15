package com.yj2025.gateway.proxy.security;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import com.yj2025.gateway.proxy.PathMatcherAuthoritiesLoader;
import com.yj2025.gateway.proxy.filter.AdditionHeaderFilter;
import com.yj2025.gateway.proxy.filter.MaintenanceWebFilter;
import com.yj2025.gateway.proxy.filter.IgnoredUrlRemoveAuthorizationFilter;
import com.yj2025.gateway.proxy.security.jwt.JwtTokenConfiguration;
import com.yj2025.gateway.proxy.security.redis.OpaqueRedisTokenConfiguration;
import com.yj2025.gateway.proxy.security.rest.OpaqueRestTokenConfiguration;
import com.yj2025.gateway.proxy.utils.ServerWebExchangeContextHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;

@Configuration
@Import({JwtTokenConfiguration.class, OpaqueRedisTokenConfiguration.class, OpaqueRestTokenConfiguration.class})
public class SecurityConfiguration {

    @Autowired
    private GatewayProxyProperties properties;

    @Autowired
    private ObjectProvider<PathMatcherAuthoritiesLoader> pathMatcherAuthoritiesLoaderObjectProvider;

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
                .addFilterBefore((exchange, chain) -> chain.filter(exchange).subscriberContext(ctx -> ctx.put(ServerWebExchangeContextHolder.CONTEXT_KEY, exchange)), SecurityWebFiltersOrder.FIRST)
                .addFilterBefore(new MaintenanceWebFilter(properties), SecurityWebFiltersOrder.AUTHENTICATION) // 前置加入系统维护中过滤器
                .addFilterBefore(new IgnoredUrlRemoveAuthorizationFilter(properties), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(new AdditionHeaderFilter(), SecurityWebFiltersOrder.AUTHORIZATION) // header 信息补充过滤器
                .oauth2ResourceServer(auth -> {
                    auth.bearerTokenConverter(tokenAuthenticationConverter())
                            .accessDeniedHandler(new AccessDeniedHandler())
                            .authenticationEntryPoint(new AuthenticationEntryPoint());
                    authCustomizerObjectProvider.ifAvailable(specCustomizer -> {
                        specCustomizer.customize(auth);
                    });
                })
                .authorizeExchange(auth ->
                        auth.pathMatchers(properties.getIgnoredUrls()).permitAll()
                                .anyExchange()
                                .access(properties.getOauth2().getAuthType().getAuthorizationManager(pathMatcherAuthoritiesLoaderObjectProvider.getIfAvailable(() -> PathMatcherAuthoritiesLoader.DEFALT)))
                                .and()
                                .exceptionHandling()
                                .accessDeniedHandler(new AccessDeniedHandler())
                                .authenticationEntryPoint(new AuthenticationEntryPoint())
                );
        return http.build();
    }


}
