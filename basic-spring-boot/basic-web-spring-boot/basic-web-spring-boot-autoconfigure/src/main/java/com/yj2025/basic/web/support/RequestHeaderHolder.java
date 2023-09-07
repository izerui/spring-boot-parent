package com.yj2025.basic.web.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class RequestHeaderHolder {

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new RuntimeException("非web请求，无法获取request对象");
    }

    public static String getHeader(String header) {
        String value = getRequest().getHeader(header);
        if (value != null) {
            value = URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
        return value;
    }

    public static String getEntCode() {
        return getHeader("entCode");
    }

    public static String getEntName() {
        return getHeader("entName");
    }

    public static String getUserCode() {
        return getHeader("userCode");
    }

    public static String getUserName() {
        return getHeader("userName");
    }

    public static String getAccountCode() {
        return getHeader("accountCode");
    }

    public static String getAccountName() {
        return getHeader("accountName");
    }
    public static String getPostCode() {
        return getHeader("postCode");
    }
}
