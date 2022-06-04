package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.WxProperties;

/**
 * 通过tenantId获取第三方应用授权的auth企业的永久授权码等配置
 */
@FunctionalInterface
public interface TpAuthConfigLoader {
    /**
     * 通过tenantId加载配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.TpAuthConfig getConfig(String tenantId);
}
