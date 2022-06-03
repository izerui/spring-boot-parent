package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.config.CpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.CpConfigOperator;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.springframework.beans.factory.InitializingBean;

public class CpServiceImpl extends WxCpServiceImpl implements CpService, InitializingBean {

    @Override
    public CpConfigStorageAdpatder getStorageAdpatder() {
        return (CpConfigStorageAdpatder) getWxCpConfigStorage();
    }

    @Override
    public CpService tenant(String tenantId) {
        getStorageAdpatder().tenant(tenantId);
        return this;
    }

    @Override
    public CpConfigOperator getTenantOperator() {
        return getStorageAdpatder().getTenantOperator();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CpConfigStorageAdpatder wxCpConfigStorage = (CpConfigStorageAdpatder) getWxCpConfigStorage();
        WxProperties properties = wxCpConfigStorage.getProperties();
        CpConfigOperator tenantOperator = wxCpConfigStorage.getTenantOperator();
        if (properties.getConfigs() != null) {
            tenantOperator.setConfigs(
                    properties.getConfigs().toArray(new WxProperties.CpConfig[properties.getConfigs().size()])
            );
        }
    }
}
