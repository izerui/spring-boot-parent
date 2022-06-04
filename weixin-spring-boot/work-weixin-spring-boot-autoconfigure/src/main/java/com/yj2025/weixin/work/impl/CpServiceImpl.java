package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.ConfigStorageAdpatder;
import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpService;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.config.ConfigOperator;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.bean.ocr.WxOcrPos;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Set;

public class CpServiceImpl extends WxCpServiceImpl implements CpService, InitializingBean {

    private TpService tpService;
    private WxProperties properties;

    public void setTpService(TpService tpService) {
        this.tpService = tpService;
    }

    public void setProperties(WxProperties properties) {
        this.properties = properties;
    }

    @Override
    public ConfigStorageAdpatder getStorageAdpatder() {
        return (ConfigStorageAdpatder) getWxCpConfigStorage();
    }

    @Override
    public TpService getTpService() {
        return tpService;
    }

    @Override
    public CpService tenant(String tenantId, boolean isThirdApp) {
        getStorageAdpatder().tenant(tenantId, isThirdApp);
        return this;
    }

    @Override
    public WxProperties.CpConfig getConfig(String tenantId) {
        return getConfigOperator().getConfig(tenantId);
    }

    @Override
    public List<WxProperties.CpConfig> getConfigs() {
        return getConfigOperator().getConfigs();
    }

    @Override
    public ConfigOperator getConfigOperator() {
        return getStorageAdpatder().getConfigOperator();
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        ConfigStorageAdpatder storageAdpatder = getStorageAdpatder();
        if (!storageAdpatder.isThirdApp()) {
            return super.getAccessToken(forceRefresh);
        } else {
            if (!storageAdpatder.isAccessTokenExpired() && !forceRefresh) {
                return storageAdpatder.getAccessToken();
            }
            //access token通过第三方应用service获取
            //corpSecret对应企业永久授权码
            WxAccessToken accessToken = tpService.getCorpToken(storageAdpatder.getCorpId(), storageAdpatder.getPermanentCode());
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
        ConfigStorageAdpatder wxCpConfigStorage = (ConfigStorageAdpatder) getWxCpConfigStorage();
        ConfigOperator tenantOperator = wxCpConfigStorage.getConfigOperator();
        if (properties.getConfigs() != null) {
            tenantOperator.setConfigs(
                    properties.getConfigs().toArray(new WxProperties.CpConfig[properties.getConfigs().size()])
            );
        }
    }
}
