package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.LoginSuccessHandler;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.events.DelegatingSuccessEventListener;
import com.yj2025.oauth2.server.security.provider.PasswordAuthProvider;
import com.yj2025.oauth2.server.security.provider.QrcodeAuthProvider;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by serv on 2017/4/11.
 */
@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;
    @Autowired
    private QrcodeService qrcodeService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(
                        form -> form.disable()
                )
                .authorizeRequests(a -> a.antMatchers(
                                        // 注销
                                        "/oauth/revoke",
                                        // 登录
                                        "/oauth/token",
                                        // 暴露给第三方获取jwt公钥，用来验签(云集本身不用)
                                        "/oauth/token_key",
                                        // rest 模式验证token地址
                                        "/oauth/check_token",
                                        // 网关获取jwt证书key，用来验token
                                        "/rsa/key",
                                        // 非我的经管扫描二维码后跳转的url地址
                                        "/qrcode/redirect",
                                        // 生成二维码
                                        "/qrcode/generate",
                                        // 验证二维码
                                        "/qrcode/validate"
                                )
                                .permitAll()
                                .anyRequest()
                                .denyAll()
                );

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
            auth.authenticationProvider(authenticationProvider);
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoderMatchor passwordCheckMatchor() {
        return new DefaultPasswordEncoderMatchor(passwordEncoder());
    }

    /**
     * 用户密码登录验证器
     *
     * @param userDetailsServiceAdapter
     * @param passwordCheckMatchorProvider
     * @return
     */
    @Order(-1)
    @Bean
    public AuthenticationProvider passwordAuthProvider(UserDetailsServiceAdapter userDetailsServiceAdapter,
                                                       ObjectProvider<PasswordEncoderMatchor> passwordCheckMatchorProvider) {
        return new PasswordAuthProvider(userDetailsServiceAdapter, passwordCheckMatchorProvider);
    }

    /**
     * 扫码登录验证器
     *
     * @param userDetailsServiceAdapter
     * @param qrcodeService
     * @return
     */
    @Bean
    public AuthenticationProvider qrcodeAuthProvider(UserDetailsServiceAdapter userDetailsServiceAdapter,
                                                     QrcodeService qrcodeService) {
        return new QrcodeAuthProvider(userDetailsServiceAdapter, qrcodeService);
    }

    @Bean
    public QrcodeService qrcodeService(RedisConnectionFactory redisConnectionFactory) {
        return new QrcodeService(redisConnectionFactory);
    }

    /**
     * 获取用户选择器
     *
     * @param userDetailsLoader 用户加载器
     * @param qrcodeService     二维码服务
     * @return
     */
    @Bean
    public UserDetailsServiceAdapter userDetailsService(@NonNull UserDetailsRemoteLoader userDetailsLoader,
                                                 QrcodeService qrcodeService) {
        return new UserDetailsServiceAdapter(userDetailsLoader, qrcodeService);
    }

    @Bean
    public DelegatingSuccessEventListener successHandler(ObjectProvider<LoginSuccessHandler> loginSuccessHandlers) {
        return new DelegatingSuccessEventListener(loginSuccessHandlers.iterator());
    }

}
