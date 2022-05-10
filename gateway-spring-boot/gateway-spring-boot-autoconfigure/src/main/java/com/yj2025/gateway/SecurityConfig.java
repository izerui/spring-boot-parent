package com.yj2025.gateway;

import com.google.gson.Gson;
import com.yj2025.gateway.filter.HeaderFilter;
import com.yj2025.gateway.filter.IgnoreUrlsRemoveAuthorizationHeaderFilter;
import com.yj2025.gateway.filter.MaintenanceWebFilter;
import com.yj2025.gateway.security.RedisTokenIntrospector;
import com.yj2025.gateway.security.WebAuthenticationEntryPoint;
import com.yj2025.gateway.security.WebAuthorizationManager;
import com.yj2025.gateway.security.WebServerAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;

@Configuration
public class SecurityConfig {

    @Autowired
    private WebAuthorizationManager authorizationManager;

    @Value("${maintenance:false}")
    private Boolean maintenance;

    @Autowired
    private Gson gson;

    @Autowired
    private GatewayProxyProperties gatewayProxyProperties;

    @Autowired
    private RedisTokenStore redisTokenStore;

    @Autowired
    private ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter;

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

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
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        WebAuthenticationEntryPoint authenticationEntryPoint = new WebAuthenticationEntryPoint(gson);
        WebServerAccessDeniedHandler serverAccessDeniedHandler = new WebServerAccessDeniedHandler(gson);
        http.csrf(c -> c.disable())
                .cors(s -> s.configurationSource(new UrlBasedCorsConfigurationSource(new PathPatternParser()) {{
                    this.registerCorsConfiguration("/**", buildConfig());
                }}))
                .requestCache().disable()
                .addFilterBefore(new MaintenanceWebFilter(maintenance, gson), SecurityWebFiltersOrder.AUTHENTICATION) // 前置加入系统维护中过滤器
                .addFilterBefore(new IgnoreUrlsRemoveAuthorizationHeaderFilter(gatewayProxyProperties), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(new HeaderFilter(), SecurityWebFiltersOrder.AUTHORIZATION) // header 信息补充过滤器
                .oauth2ResourceServer(auth ->
                        auth.bearerTokenConverter(tokenAuthenticationConverter)
                                .opaqueToken()
                                .introspector(new RedisTokenIntrospector(redisTokenStore))
                                .and()
                                .accessDeniedHandler(serverAccessDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeExchange(auth ->
                        auth.pathMatchers(gatewayProxyProperties.getIgnoredUrls()).permitAll()
                                .anyExchange().access(authorizationManager)
                                .and()
                                .exceptionHandling().accessDeniedHandler(serverAccessDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint)
                );
        return http.build();
    }

    @Bean
    public RedisTokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory) {
        return new RedisTokenStore(redisConnectionFactory);
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

}
