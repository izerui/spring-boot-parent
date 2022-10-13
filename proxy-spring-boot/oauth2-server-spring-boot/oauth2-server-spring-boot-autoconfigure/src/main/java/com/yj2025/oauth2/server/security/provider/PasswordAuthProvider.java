package com.yj2025.oauth2.server.security.provider;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.security.UserDetailsServiceAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 密码登录验证器
 */
public class PasswordAuthProvider extends AbstractAuthenticationProvider implements UserSelector {

    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    private ObjectProvider<PasswordEncoderMatchor> passwordEncoderMatchorObjectProvider;

    public PasswordAuthProvider(UserDetailsServiceAdapter userDetailsServiceAdapter, ObjectProvider<PasswordEncoderMatchor> passwordEncoderMatchorObjectProvider) {
        this.userDetailsServiceAdapter = userDetailsServiceAdapter;
        this.passwordEncoderMatchorObjectProvider = passwordEncoderMatchorObjectProvider;
        this.setPreAuthenticationChecks(new DefaultPreUserDetailsChecker());
        this.setPostAuthenticationChecks(new DefaultPostUserDetailsChecker());
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        User user = (User) userDetails;
        String inputPassword = authentication.getCredentials().toString();
        if (!passwordEncoderMatchorObjectProvider.getIfAvailable().matches(inputPassword, user.getPassword(), user.getAdditionalSalt())) {
            throwCredentialsExpiredExceptionBlock("用户名或密码错误!");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (username == null) {
            throwUsernameNotFoundExceptionNext("请输入用户名!");
        }
        if (authentication.getCredentials() == null || StringUtils.isEmpty(authentication.getCredentials().toString())) {
            throwPreAuthenticatedCredentialsNotFoundExceptionNext("用户密码为空,继续进行二维码登录验证!");
        }
        UserDetails user = this.userDetailsServiceAdapter.loadUserByUsername(username, this);
        if (user == null) {
            throwAccountExpiredExceptionBlock("用户名或密码错误!");
        }
        return user;
    }

    /**
     * 如果是切换账套的话（刷新token-指定usercode），读取指定的usercode
     *
     * @return
     */
    @Override
    public Optional<String> getSelector() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            return Optional.ofNullable(request.getParameter("usercode"));
        }
        return Optional.empty();
    }

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.USER_CODE_SELECTOR;
    }
}
