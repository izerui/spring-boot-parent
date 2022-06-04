package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.WxProperties;

/**
 * 通过tenantId获取外部配置
 */
@FunctionalInterface
public interface CpConfigLoader {
    /**
     * 通过tenantId加载配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.CpConfig getConfig(String tenantId);
}
