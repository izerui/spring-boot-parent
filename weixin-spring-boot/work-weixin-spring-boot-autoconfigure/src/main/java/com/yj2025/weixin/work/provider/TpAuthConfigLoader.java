package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.WxProperties;

public interface TpAuthConfigLoader {
    /**
     * 通过tenantId加载配置
     * @param tenantId
     * @return
     */
    WxProperties.TpAuthConfig getConfig(String tenantId);
}
