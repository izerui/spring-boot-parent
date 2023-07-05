package com.yj2025.jdbc;

public class TenantThreadLocal {
    private final static ThreadLocal<String> tenantLocal = new InheritableThreadLocal<>();

    public static void setTenantId(String tenantId) {
        tenantLocal.set(tenantId);
    }

    public static String getTenantId() {
        return tenantLocal.get();
    }
}
