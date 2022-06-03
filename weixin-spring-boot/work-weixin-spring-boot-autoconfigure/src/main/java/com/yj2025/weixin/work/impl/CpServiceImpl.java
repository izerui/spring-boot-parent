package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.config.CpConfigOperator;
import com.yj2025.weixin.work.config.CpConfigStorageAdpatder;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import org.springframework.beans.factory.InitializingBean;

public class CpServiceImpl extends WxCpServiceImpl implements CpService, InitializingBean {

    private WxCpTpService tpService;

    public void setTpService(WxCpTpService tpService) {
        this.tpService = tpService;
    }

    @Override
    public CpConfigStorageAdpatder getStorageAdpatder() {
        return (CpConfigStorageAdpatder) getWxCpConfigStorage();
    }

    @Override
    public CpService tenant(String tenantId, boolean isThirdApp) {
        getStorageAdpatder().tenant(tenantId, isThirdApp);
        return this;
    }

    @Override
    public CpConfigOperator getTenantOperator() {
        return getStorageAdpatder().getTenantOperator();
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        CpConfigStorageAdpatder storageAdpatder = getStorageAdpatder();
        if (storageAdpatder.isThirdApp()) {
            return super.getAccessToken(forceRefresh);
        } else {
            if (!storageAdpatder.isAccessTokenExpired() && !forceRefresh) {
                return storageAdpatder.getAccessToken();
            }
            //access token通过第三方应用service获取
            //corpSecret对应企业永久授权码
            WxAccessToken accessToken = tpService.getCorpToken(this.configStorage.getCorpId(), this.configStorage.getCorpSecret());
            storageAdpatder.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
            return storageAdpatder.getAccessToken();
        }
    }

    /**
     * 初始化自建应用的配置放入缓存
     *
     * @throws Exception
     */
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
