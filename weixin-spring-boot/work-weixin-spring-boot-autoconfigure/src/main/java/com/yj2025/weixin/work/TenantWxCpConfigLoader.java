package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.TenantWxCpConfig;

@FunctionalInterface
public interface TenantWxCpConfigLoader {
    /**
     * 通过tenantId加载配置
     * @param tenantId
     * @return
     */
    TenantWxCpConfig getConfig(String tenantId);
}
