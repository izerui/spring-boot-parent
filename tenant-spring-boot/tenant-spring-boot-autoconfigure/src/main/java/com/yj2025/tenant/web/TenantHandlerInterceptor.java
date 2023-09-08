package com.yj2025.tenant.web;

import com.yj2025.tenant.TenantHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TenantHandlerInterceptor implements HandlerInterceptor {

    private final static String TENANT_KEY = "entCode";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String value = request.getHeader(TENANT_KEY);
        if (value != null) {
            String tenantId = URLDecoder.decode(value, StandardCharsets.UTF_8);
            TenantHolder.setTenantId(tenantId);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
