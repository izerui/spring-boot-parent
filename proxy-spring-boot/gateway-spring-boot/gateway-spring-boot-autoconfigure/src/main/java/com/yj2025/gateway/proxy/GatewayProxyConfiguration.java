package com.yj2025.gateway.proxy;

import com.yj2025.gateway.proxy.controller.ProxyQrcodeController;
import com.yj2025.gateway.proxy.controller.ProxyTokenKeyController;
import com.yj2025.gateway.proxy.filter.ProxyRemoveAuthorizationGatewayGlobalFilter;
import com.yj2025.gateway.proxy.filter.RelaxedQueryCharsWebServerCustomize;
import com.yj2025.gateway.proxy.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 网关基础封装配置
 * 1.  基于reactive异步驱动代理后端服务
 * 2.  整合oauth2认证资源服务
 * 3.  代理认证服务的部分接口
 * 4.  支持维护模式关闭入口、支持维护模式状态下白名单访问
 * 5.  token认证通过后，代理后端请求时补充用户身份等字段信息到请求header中
 * 6.  支持特殊字符的请求例如 [] 等
 * 7.  支持三种token认证模式：
 *     本地认证jwt token: 首次请求jwt证书密钥key(缓存本地)，通过key验证token
 *     通过redis认证Opaque token: 通过redis直接反序列化获取登录身份信息
 *     通过rest请求认证Opaque token: 每次获取到token后，通过rest请求oauth2-server服务，远程认证身份信息
 * 8.  以上三种认证模式都支持通过JwtAuthenticationToken或者BearerTokenAuthentication获取authorities权限集合，进行访问请求权限控制。
 * 9.  支持注册中心远程调用认证服务器
 * 10. 支持启动加载全局权限控制,通过自定义扩展实现。格式：urlMatcher:authorities
 * 11. access_token 支持 Bearer模式及url参数传递
 * 12. 忽略权限校验的地址，会移除授权信息，直接访问, 无法获取到登录用户信息
 * 13. 所有请求支持跨域访问
 */
@EnableWebFluxSecurity
@Configuration
@EnableConfigurationProperties(GatewayProxyProperties.class)
@Import({SecurityConfiguration.class, ProxyTokenKeyController.class, ProxyQrcodeController.class})
public class GatewayProxyConfiguration {

    @LoadBalanced
    @Bean
    @Primary
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RelaxedQueryCharsWebServerCustomize relaxedQueryCharsWebServerCustomize() {
        return new RelaxedQueryCharsWebServerCustomize();
    }

    @Bean
    public ProxyRemoveAuthorizationGatewayGlobalFilter proxyRemoveAuthorizationGatewayFilterFactory() {
        return new ProxyRemoveAuthorizationGatewayGlobalFilter();
    }
}
