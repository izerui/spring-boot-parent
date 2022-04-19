package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.*;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.springframework.beans.factory.InitializingBean;

public class TenantWxCpServiceImpl extends WxCpServiceImpl implements TenantWxCpService, InitializingBean {
    @Override
    public TenantWxCpService tenant(String tenantId) {
        TenantWxCpConfigStorageAdpatder wxCpConfigStorage = (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
        wxCpConfigStorage.tenant(tenantId);
        return this;
    }

    @Override
    public String getTenantIdByAgentId(String agentId) {
        TenantWxCpConfigStorageAdpatder wxCpConfigStorage = (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
        return wxCpConfigStorage.getTenantIdByAgentId(agentId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TenantWxCpConfigStorageAdpatder wxCpConfigStorage = (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
        WorkWeixinProperties properties = wxCpConfigStorage.getProperties();
        TenantConfigOperator configOperator = wxCpConfigStorage.getConfigOperator();
        configOperator.setConfigs(
                properties.getConfigs().toArray(new TenantConfig[properties.getConfigs().size()])
        );
    }
}
