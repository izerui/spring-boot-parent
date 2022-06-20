package com.yj2025.basic.controller;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public abstract class BasicController {
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new RuntimeException("非web请求，无法获取request对象");
    }

    public String getRequestHeader(String header) {
        return getRequest().getHeader(header);
    }

    public String getEntCode() {
        return getRequest().getHeader(URLDecoder.decode("entCode", StandardCharsets.UTF_8));
    }

    public String getEntName() {
        return getRequest().getHeader(URLDecoder.decode("entName", StandardCharsets.UTF_8));
    }

    public String getUserCode() {
        return getRequest().getHeader(URLDecoder.decode("userCode", StandardCharsets.UTF_8));
    }

    public String getUserName() {
        return getRequest().getHeader(URLDecoder.decode("userName", StandardCharsets.UTF_8));
    }

    public String getAccountCode() {
        return getRequest().getHeader(URLDecoder.decode("accountCode", StandardCharsets.UTF_8));
    }

    public String getAccountName() {
        return getRequest().getHeader(URLDecoder.decode("accountName", StandardCharsets.UTF_8));
    }
}
