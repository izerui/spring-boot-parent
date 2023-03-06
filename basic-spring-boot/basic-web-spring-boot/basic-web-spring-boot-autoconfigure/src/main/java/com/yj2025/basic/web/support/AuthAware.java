package com.yj2025.basic.web.support;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public interface AuthAware {

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new RuntimeException("非web请求，无法获取request对象");
    }

    private String getHeader(String header) {
        String value = getRequest().getHeader(header);
        if (value != null) {
            value = URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
        return value;
    }

    default String getEntCode() {
        return getHeader("entCode");
    }

    default String getEntName() {
        return getHeader("entName");
    }

    default String getUserCode() {
        return getHeader("userCode");
    }

    default String getUserName() {
        return getHeader("userName");
    }

    default String getAccountCode() {
        return getHeader("accountCode");
    }

    default String getAccountName() {
        return getHeader("accountName");
    }
    default String getPostCode() {
        return getHeader("postCode");
    }

}
