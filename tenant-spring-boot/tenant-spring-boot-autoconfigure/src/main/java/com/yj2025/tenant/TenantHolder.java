package com.yj2025.tenant;

public class TenantHolder {

    private final static ThreadLocal<String> tenantLocal = new InheritableThreadLocal<>();

    private final static ThreadLocal<String> yearLocal = new InheritableThreadLocal<>();

    public static void setTenantId(String tenantId) {
        tenantLocal.set(tenantId);
    }

    /**
     * 调用该方法之前请先在入口方法上面声明{@link Tenant}注解
     *
     * @return
     */
    public static String getTenantId() {
        return tenantLocal.get();
    }


    public static void setYear(String year) {
        yearLocal.set(year);
    }

    public static String getYear() {
        return yearLocal.get();
    }

}
