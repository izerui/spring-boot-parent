package com.yj2025.basic.web;

import com.yj2025.basic.web.support.RequestHeaderHolder;
import jakarta.servlet.http.HttpServletRequest;

public class WebRequestContext {

    public HttpServletRequest getRequest() {
        return RequestHeaderHolder.getRequest();
    }

    public String getHeader(String header) {
        return RequestHeaderHolder.getHeader(header);
    }

    public String getEntCode() {
        return RequestHeaderHolder.getEntCode();
    }

    public String getEntName() {
        return RequestHeaderHolder.getEntName();
    }

    public String getUserCode() {
        return RequestHeaderHolder.getUserCode();
    }

    public String getUserName() {
        return RequestHeaderHolder.getUserName();
    }

    public String getAccountCode() {
        return RequestHeaderHolder.getAccountCode();
    }

    public String getAccountName() {
        return RequestHeaderHolder.getAccountName();
    }

    public String getPostCode() {
        return RequestHeaderHolder.getPostCode();
    }
}
