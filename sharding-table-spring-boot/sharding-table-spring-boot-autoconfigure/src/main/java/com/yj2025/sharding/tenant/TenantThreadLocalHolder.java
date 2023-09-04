package com.yj2025.sharding.tenant;

public class TenantThreadLocalHolder {

    private final static ThreadLocal<String> tenantLocal = new InheritableThreadLocal<>();

    public static void setTenantId(String tenantId) {
        tenantLocal.set(tenantId);
    }

    /**
     * 调用该方法之前请先在入口方法上面声明{@link ShardingTenant}注解
     *
     * @return
     */
    public static String getTenantId() {
        return tenantLocal.get();
    }

}
