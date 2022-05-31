package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.*;
import com.yj2025.weixin.work.config.TenantConfig;
import com.yj2025.weixin.work.config.TenantConfigOperator;
import com.yj2025.weixin.work.config.TenantRuntimeOperator;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageAdpatder;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.springframework.beans.factory.InitializingBean;

public class TenantWxCpServiceImpl extends WxCpServiceImpl implements TenantWxCpService, InitializingBean {

    @Override
    public TenantWxCpConfigStorageAdpatder getStorageAdpatder() {
        return (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
    }

    @Override
    public String tenantId() {
        return getStorageAdpatder().tenantId();
    }

    @Override
    public TenantWxCpService tenant(String tenantId) {
        getStorageAdpatder().tenant(tenantId);
        return this;
    }

    @Override
    public TenantConfigOperator getConfigOperator() {
        return getStorageAdpatder().getConfigOperator();
    }

    @Override
    public TenantRuntimeOperator getRuntimeOperator() {
        return getStorageAdpatder().getRuntimeOperator();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TenantWxCpConfigStorageAdpatder wxCpConfigStorage = (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
        WorkWeixinProperties properties = wxCpConfigStorage.getProperties();
        TenantConfigOperator configOperator = wxCpConfigStorage.getConfigOperator();
        if (properties.getConfigs() != null) {
            configOperator.setConfigs(
                    properties.getConfigs().toArray(new TenantConfig[properties.getConfigs().size()])
            );
        }
    }
}
