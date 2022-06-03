package com.yj2025.weixin.work.config;

import com.yj2025.weixin.work.provider.CpConfigLoader;
import com.yj2025.weixin.work.WxProperties;
import lombok.Getter;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.constant.WxCpApiPathConsts;
import org.springframework.beans.factory.ObjectProvider;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * cp适配器
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
@ThreadSafe
public class CpConfigStorageAdpatder implements WxCpConfigStorage {
    // 当未指定租户ID的时候使用的默认的租户ID
    private final static String DEFAULT_TENANT_ID = "default";

    // 当前线程使用的tenantId(公司编号)
    protected final static InheritableThreadLocal<String> INHERITABLE_THREAD_ACTIVE_TENANT_ID;

    static {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID = new InheritableThreadLocal<>();
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(DEFAULT_TENANT_ID);
    }

    @Getter
    protected CpConfigOperator tenantOperator;
    @Getter
    protected WxProperties properties;
    protected ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders;
    private ObjectProvider<CpConfigLoader> tenantWxCpConfigLoaders;

    public CpConfigStorageAdpatder(CpConfigOperator tenantOperator,
                                   WxProperties properties,
                                   ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                   ObjectProvider<CpConfigLoader> tenantWxCpConfigLoaders) {
        this.tenantOperator = tenantOperator;
        this.properties = properties;
        this.apacheHttpClientBuilders = apacheHttpClientBuilders;
        this.tenantWxCpConfigLoaders = tenantWxCpConfigLoaders;
    }

    /**
     * 当前请求线程切换使用的租户配置
     *
     * @param tenantId
     * @return
     */
    public CpConfigStorageAdpatder tenant(String tenantId) {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(tenantId);
        if (!tenantOperator.isExistConfig(tenantId)) {
            WxProperties.CpConfig t = getIfNotExists(tenantId);
            if (t == null) {
                throw new RuntimeException("无法获取tenantId:[" + tenantId + "]相应的配置");
            }
            tenantOperator.setConfigs(t);
        }
        return this;
    }

    public String tenantId() {
        return INHERITABLE_THREAD_ACTIVE_TENANT_ID.get();
    }

    @Deprecated
    public void setBaseApiUrl(String baseUrl) {
        throw new UnsupportedOperationException();
    }

    public String getApiUrl(String path) {
        return WxCpApiPathConsts.DEFAULT_CP_BASE_URL + path;
    }

    private WxProperties.CpConfig getIfNotExists(String tenantId) {
        AtomicReference<WxProperties.CpConfig> config = new AtomicReference<>();
        tenantWxCpConfigLoaders.ifAvailable(loader -> {
            config.set(loader.getConfig(tenantId));
        });
        return config.get();
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
