package com.yj2025.basic.web.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        return getRequest().getHeader(header);
    }

    @JsonIgnore
    default String getEntCode() {
        return URLDecoder.decode(getHeader("entCode"), StandardCharsets.UTF_8);
    }

    @JsonIgnore
    default String getEntName() {
        return URLDecoder.decode(getHeader("entName"), StandardCharsets.UTF_8);
    }

    @JsonIgnore
    default String getUserCode() {
        return URLDecoder.decode(getHeader("userCode"), StandardCharsets.UTF_8);
    }

    @JsonIgnore
    default String getUserName() {
        return URLDecoder.decode(getHeader("userName"), StandardCharsets.UTF_8);
    }

    @JsonIgnore
    default String getAccountCode() {
        return URLDecoder.decode(getHeader("accountCode"), StandardCharsets.UTF_8);
    }

    @JsonIgnore
    default String getAccountName() {
        return URLDecoder.decode(getHeader("accountName"), StandardCharsets.UTF_8);
    }

}
