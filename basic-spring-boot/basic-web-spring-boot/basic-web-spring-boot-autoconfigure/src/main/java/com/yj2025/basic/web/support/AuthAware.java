package com.yj2025.basic.web.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;
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

    @JsonIgnore
    default String getEntCode() {
        return getHeader("entCode");
    }

    @JsonIgnore
    default String getEntName() {
        return getHeader("entName");
    }

    @JsonIgnore
    default String getUserCode() {
        return getHeader("userCode");
    }

    @JsonIgnore
    default String getUserName() {
        return getHeader("userName");
    }

    @JsonIgnore
    default String getAccountCode() {
        return getHeader("accountCode");
    }

    @JsonIgnore
    default String getAccountName() {
        return getHeader("accountName");
    }

}
