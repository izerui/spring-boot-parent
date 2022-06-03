package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.CpConfig;

@FunctionalInterface
public interface CpConfigLoader {
    /**
     * 通过tenantId加载配置
     * @param tenantId
     * @return
     */
    CpConfig getConfig(String tenantId);
}
