package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantWxCpService;
import com.yj2025.weixin.work.WorkWeixinProperties;
import com.yj2025.weixin.work.config.TenantWxCpConfig;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageOperator;
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
    public TenantWxCpConfigStorageOperator getTenantOperator() {
        return getStorageAdpatder().getTenantOperator();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TenantWxCpConfigStorageAdpatder wxCpConfigStorage = (TenantWxCpConfigStorageAdpatder) getWxCpConfigStorage();
        WorkWeixinProperties properties = wxCpConfigStorage.getProperties();
        TenantWxCpConfigStorageOperator tenantOperator = wxCpConfigStorage.getTenantOperator();
        if (properties.getConfigs() != null) {
            tenantOperator.setConfigs(
                    properties.getConfigs().toArray(new TenantWxCpConfig[properties.getConfigs().size()])
            );
        }
    }
}
