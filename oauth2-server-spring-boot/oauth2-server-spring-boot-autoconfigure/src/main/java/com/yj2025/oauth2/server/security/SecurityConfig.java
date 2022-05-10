package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.UserDetailsLoader;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Created by serv on 2017/4/11.
 */
@Slf4j
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin()
                .and()
                .authorizeRequests(a -> a.antMatchers(
                                "/oauth/revoke",
                                "/oauth/token")
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
        return new FormDaoAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService(UserDetailsLoader userDetailsLoader) {
        return username -> {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return userDetailsLoader.loadUserByUsername(username, request.getParameter("usercode"));
        };
    }

}
