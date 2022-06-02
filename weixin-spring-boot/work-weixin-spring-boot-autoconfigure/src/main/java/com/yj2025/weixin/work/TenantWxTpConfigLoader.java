package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.TenantWxTpConfig;

@FunctionalInterface
public interface TenantWxTpConfigLoader {
    /**
     * 通过tenantId加载配置
     *
     * @param tenantId
     * @return
     */
    TenantWxTpConfig getConfig(String tenantId);
}
