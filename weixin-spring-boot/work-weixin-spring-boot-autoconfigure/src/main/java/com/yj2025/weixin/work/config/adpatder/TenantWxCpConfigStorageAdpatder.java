package com.yj2025.weixin.work.config.adpatder;

import com.yj2025.weixin.work.WorkWeixinProperties;
import com.yj2025.weixin.work.config.TenantWxCpConfigStorageOperator;
import lombok.Getter;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.constant.WxCpApiPathConsts;
import org.springframework.beans.factory.ObjectProvider;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.concurrent.locks.Lock;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@ThreadSafe
public class TenantWxCpConfigStorageAdpatder extends AbstractTenantConfigStorageAdpatder<TenantWxCpConfigStorageOperator> implements WxCpConfigStorage {


    public TenantWxCpConfigStorageAdpatder(TenantWxCpConfigStorageOperator tenantOperator,
                                           WorkWeixinProperties properties,
                                           ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders) {
        super(tenantOperator, properties, apacheHttpClientBuilders);
    }

    @Deprecated
    @Override
    public void setBaseApiUrl(String baseUrl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getApiUrl(String path) {
        return WxCpApiPathConsts.DEFAULT_CP_BASE_URL + path;
    }

    @Override
    public String getAccessToken() {
        return tenantOperator.getAccessToken(tenantId());
    }

    @Override
    public Lock getAccessTokenLock() {
        return tenantOperator.getAccessTokenLock(tenantId());
    }

    @Override
    public boolean isAccessTokenExpired() {
        return tenantOperator.isAccessTokenExpired(tenantId());
    }

    @Override
    public void expireAccessToken() {
        tenantOperator.expireAccessToken(tenantId());
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        tenantOperator.updateAccessToken(tenantId(), accessToken);
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresIn) {
        WxAccessToken wat = new WxAccessToken();
        wat.setAccessToken(accessToken);
        wat.setExpiresIn(expiresIn);
        tenantOperator.updateAccessToken(tenantId(), wat);
    }

    @Override
    public String getJsapiTicket() {
        return tenantOperator.getJsapiTicket(tenantId());
    }

    @Override
    public Lock getJsapiTicketLock() {
        return tenantOperator.getJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return tenantOperator.isJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireJsapiTicket() {
        tenantOperator.expireJsapiTicket(tenantId());
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        tenantOperator.setJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket() {
        return tenantOperator.getAgentJsapiTicket(tenantId());
    }

    @Override
    public Lock getAgentJsapiTicketLock() {
        return tenantOperator.getAgentJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isAgentJsapiTicketExpired() {
        return tenantOperator.isAgentJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireAgentJsapiTicket() {
        tenantOperator.expireAgentJsapiTicket(tenantId());
    }

    @Override
    public void updateAgentJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        tenantOperator.updateAgentJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getCorpId() {
        return tenantOperator.getCorpId(tenantId());
    }

    @Override
    public String getCorpSecret() {
        return tenantOperator.getCorpSecret(tenantId());
    }

    @Override
    public Integer getAgentId() {
        return tenantOperator.getAgentId(tenantId());
    }

    @Override
    public String getToken() {
        return tenantOperator.getToken(tenantId());
    }

    @Override
    public String getAesKey() {
        return tenantOperator.getAesKey(tenantId());
    }

    @Override
    public String getMsgAuditLibPath() {
        return tenantOperator.getMsgAuditLibPath(tenantId());
    }

    @Override
    public long getExpiresTime() {
        return tenantOperator.getExpiresTime(tenantId());
    }

    @Override
    public String getOauth2redirectUri() {
        return tenantOperator.getOauth2redirectUri(tenantId());
    }

    @Override
    public String getHttpProxyHost() {
        return properties.getProxy().getHttpProxyHost();
    }

    @Override
    public int getHttpProxyPort() {
        return properties.getProxy().getHttpProxyPort();
    }

    @Override
    public String getHttpProxyUsername() {
        return properties.getProxy().getHttpProxyUsername();
    }

    @Override
    public String getHttpProxyPassword() {
        return properties.getProxy().getHttpProxyPassword();
    }

    @Override
    public File getTmpDirFile() {
        return properties.getTmpDirFile();
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return apacheHttpClientBuilders.getIfAvailable();
    }

    @Override
    public boolean autoRefreshToken() {
        return true;
    }

    @Override
    public String getWebhookKey() {
        return tenantOperator.getWebhookKey(tenantId());
    }
}
