package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.LoginSuccessHandler;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.events.DelegatingSuccessEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by serv on 2017/4/11.
 */
@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;
    @Autowired
    private DelegatingSuccessEventListener delegatingSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(
                        form -> form.disable()
                )
                .authorizeRequests(a -> a.antMatchers(
                                "/oauth/revoke",
                                "/oauth/token",
                                "/oauth/token_key",
                                "/oauth/check_token",
                                "/rsa/key")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
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

    @Order(0)
    @Bean
    public AuthenticationProvider formAuthenticationProvider(UserDetailsService userDetailsService) {
        return new UserAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService(UserDetailsRemoteLoader userDetailsLoader) {
        return username -> {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return userDetailsLoader.loadUserByUsername(username, Optional.ofNullable(request.getParameter("usercode")));
        };
    }

    @Bean
    public DelegatingSuccessEventListener successHandler(ObjectProvider<LoginSuccessHandler> loginSuccessHandlers) {
        return new DelegatingSuccessEventListener(loginSuccessHandlers.iterator());
    }

}
