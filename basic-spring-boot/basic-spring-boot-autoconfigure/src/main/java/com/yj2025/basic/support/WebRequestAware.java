package com.yj2025.basic.support;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public interface WebRequestAware {

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new RuntimeException("非web请求，无法获取request对象");
    }

    default String getRequestHeader(String header) {
        return getRequest().getHeader(header);
    }

    default String getEntCode() {
        return getRequest().getHeader(URLDecoder.decode("entCode", StandardCharsets.UTF_8));
    }

    default String getEntName() {
        return getRequest().getHeader(URLDecoder.decode("entName", StandardCharsets.UTF_8));
    }

    default String getUserCode() {
        return getRequest().getHeader(URLDecoder.decode("userCode", StandardCharsets.UTF_8));
    }

    default String getUserName() {
        return getRequest().getHeader(URLDecoder.decode("userName", StandardCharsets.UTF_8));
    }

    default String getAccountCode() {
        return getRequest().getHeader(URLDecoder.decode("accountCode", StandardCharsets.UTF_8));
    }

    default String getAccountName() {
        return getRequest().getHeader(URLDecoder.decode("accountName", StandardCharsets.UTF_8));
    }
}
