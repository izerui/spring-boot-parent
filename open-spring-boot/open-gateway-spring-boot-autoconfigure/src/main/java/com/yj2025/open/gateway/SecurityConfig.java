package com.yj2025.open.gateway;

import com.yj2025.open.commons.ClientStore;
import com.yj2025.open.commons.RedisClientStore;
import com.yj2025.open.gateway.endpoint.TokenProxyEndpoint;
import com.yj2025.open.gateway.filter.HeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;

import static com.yj2025.open.gateway.utils.ExchangeUtils.wirteErrorResponse;

/**
 * @author liuyuhua
 */
@Configuration
@Import(TokenProxyEndpoint.class)
@EnableWebFluxSecurity
@EnableConfigurationProperties(GatewayProperties.class)
public class SecurityConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private GatewayProperties properties;

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    private CorsConfiguration corsBuildConfig() {
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
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsBuildConfig());

        http.csrf(c -> c.disable())
                .cors(s -> s.configurationSource(source))
                .requestCache().disable()
                // header 信息补充过滤器
                .addFilterAfter(new HeaderFilter(clientStore()), SecurityWebFiltersOrder.AUTHORIZATION)
                .oauth2ResourceServer(auth ->
                        auth
                                .bearerTokenConverter(tokenAuthenticationConverter())
                                .jwt()
                                .jwtDecoder(
                                        NimbusReactiveJwtDecoder
                                                .withJwkSetUri("lb://" + properties.getOauthApp() + "/rsa/key")
                                                .webClient(webClientBuilder().build())
                                                .build()
                                )
                                .and()
                                .accessDeniedHandler((exchange, denied) -> wirteErrorResponse(exchange, "ACCESS_DENIED", "未授权!"))
                                .authenticationEntryPoint((exchange, e) -> wirteErrorResponse(exchange, "AUTHORIZATION_ERROR", "authentication无效"))
                );
        return http.build();
    }

    @Bean
    public ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter() {
        ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter = new ServerBearerTokenAuthenticationConverter();
        tokenAuthenticationConverter.setAllowUriQueryParameter(true);
        return tokenAuthenticationConverter;
    }

    @Bean
    public ClientStore clientStore() {
        return new RedisClientStore(redisConnectionFactory);
    }


}
