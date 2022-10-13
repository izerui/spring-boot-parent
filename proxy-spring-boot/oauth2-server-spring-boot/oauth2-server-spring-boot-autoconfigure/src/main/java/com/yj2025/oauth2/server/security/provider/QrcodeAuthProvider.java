package com.yj2025.oauth2.server.security.provider;

import com.yj2025.oauth2.server.security.UserDetailsServiceAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static com.yj2025.oauth2.security.support.QrcodeConstants.QRCODE_TICKET_KEY;

/**
 * 二维码登录认证器
 */
public class QrcodeAuthProvider extends AbstractAuthenticationProvider implements UserSelector {

    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    private QrcodeService qrcodeService;

    public QrcodeAuthProvider(UserDetailsServiceAdapter userDetailsServiceAdapter,
                              QrcodeService qrcodeService) {
        this.userDetailsServiceAdapter = userDetailsServiceAdapter;
        this.qrcodeService = qrcodeService;
        this.setPreAuthenticationChecks(new DefaultPreUserDetailsChecker());
        this.setPostAuthenticationChecks(new DefaultPostUserDetailsChecker());
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            throwInternalAuthenticationServiceExceptionNext("非web请求,被拒绝!");
        }
        String username = authentication.getName();
        if (username == null) {
            throwUsernameNotFoundExceptionNext("扫码登录失败");
        }
        String qrCodeTicket = (String) request.getSession().getAttribute(QRCODE_TICKET_KEY);
        Map<String, Object> qrCodeMapValue = qrcodeService.getAndRemoveTicketValue(qrCodeTicket);
        if (qrCodeMapValue == null) {
            throwBadCredentialsExceptionNext("扫码无效,请重试!");
        }
        String accountName = (String) qrCodeMapValue.get("accountName");
        if ("".equals(accountName) || "unknown".equals(accountName)) {
            throwBadCredentialsExceptionNext("扫码无效,请重试!");
        }
        if (!accountName.equals(username)) {
            throwBadCredentialsExceptionNext("扫码无效,请自重!");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (username == null) {
            throwUsernameNotFoundExceptionNext("请输入用户名!");
        }
        UserDetails user = this.userDetailsServiceAdapter.loadUserByUsername(username, this);
        if (user == null) {
            throwAccountExpiredExceptionBlock("用户名或密码错误!");
        }
        return user;
    }

    /**
     * 从cookie中获取ticket，然后读取qrcode服务中的记录的entcode
     *
     * @return
     */
    @Override
    public Optional<String> getSelector() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie cookie = WebUtils.getCookie(request, QRCODE_TICKET_KEY);
        if (cookie == null) {
            return Optional.empty();
        }
        String qrCodeTicket = cookie.getValue();
        QrcodeStatus qrcodeStatus = qrcodeService.getQrcodeStatus(qrCodeTicket);
        return Optional.ofNullable(qrcodeStatus.getEntCode());
    }

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.ENT_CODE_SELECTOR;
    }
}
