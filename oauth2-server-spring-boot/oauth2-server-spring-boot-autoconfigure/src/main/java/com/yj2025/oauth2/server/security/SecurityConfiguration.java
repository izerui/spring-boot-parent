package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.security.support.MappingUrls;
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
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.yj2025.oauth2.security.support.QrcodeConstants.QRCODE_REDIS_KEY_PREFIX;

/**
 * Created by serv on 2017/4/11.
 */
@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(
                        form -> form.disable()
                )
                .authorizeRequests(a -> a.antMatchers(MappingUrls.OAUTH_SERVER_IGNORE_URLS)
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
    public QrcodeService qrcodeService(RedisConnectionFactory redisConnectionFactory,
                                       @Value("${spring.application.name:'CAS:QRCODE:'}") String applicationName) {
        return new QrcodeService(redisConnectionFactory, QRCODE_REDIS_KEY_PREFIX.apply(applicationName));
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
