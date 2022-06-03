package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.WxProperties;

@FunctionalInterface
public interface CpConfigLoader {
    /**
     * 通过tenantId加载配置
     * @param tenantId
     * @return
     */
    WxProperties.CpConfig getConfig(String tenantId);
}
