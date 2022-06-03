package com.yj2025.weixin.work.config;

import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.provider.CpConfigLoader;
import com.yj2025.weixin.work.provider.TpAuthConfigLoader;
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
    // 当前tenantId对应的是否是第三方app
    protected final static InheritableThreadLocal<Boolean> INHERITABLE_THREAD_ACTIVE_TENANT_TYPE;

    static {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID = new InheritableThreadLocal<>();
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(DEFAULT_TENANT_ID);

        INHERITABLE_THREAD_ACTIVE_TENANT_TYPE = new InheritableThreadLocal<>();
        INHERITABLE_THREAD_ACTIVE_TENANT_TYPE.set(false);
    }

    @Getter
    protected CpConfigOperator tenantOperator;
    @Getter
    protected WxProperties properties;
    protected ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders;
    private ObjectProvider<CpConfigLoader> cpConfigLoaders;
    private ObjectProvider<TpAuthConfigLoader> tpAuthConfigLoaders;

    public CpConfigStorageAdpatder(CpConfigOperator tenantOperator,
                                   WxProperties properties,
                                   ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                   ObjectProvider<CpConfigLoader> cpConfigLoaders,
                                   ObjectProvider<TpAuthConfigLoader> tpAuthConfigLoaders) {
        this.tenantOperator = tenantOperator;
        this.properties = properties;
        this.apacheHttpClientBuilders = apacheHttpClientBuilders;
        this.cpConfigLoaders = cpConfigLoaders;
        this.tpAuthConfigLoaders = tpAuthConfigLoaders;
    }

    /**
     * 当前请求线程切换使用的租户配置
     *
     * @param tenantId
     * @param isThirdApp
     * @return
     */
    public CpConfigStorageAdpatder tenant(String tenantId, boolean isThirdApp) {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(tenantId);
        INHERITABLE_THREAD_ACTIVE_TENANT_TYPE.set(isThirdApp);
        if (!tenantOperator.isExistConfig(tenantId)) {
            WxProperties.CpConfig t = getIfNotExists(tenantId, isThirdApp);
            if (t == null) {
                throw new RuntimeException("无法获取tenantId:[" + tenantId + "]相应的配置");
            }
            tenantOperator.setConfigs(t);
        }
        return this;
    }

    /**
     * 当前请求的tenantId对应的是否是第三方应用
     *
     * @return
     */
    public boolean isThirdApp() {
        return INHERITABLE_THREAD_ACTIVE_TENANT_TYPE.get();
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

    private WxProperties.CpConfig getIfNotExists(String tenantId, boolean isThirdApp) {
        AtomicReference<WxProperties.CpConfig> config = new AtomicReference<>();
        if (isThirdApp) {
            tpAuthConfigLoaders.ifAvailable(loader -> {
                WxProperties.TpAuthConfig tpAuthConfig = loader.getConfig(tenantId);
                config.set(new WxProperties.CpConfig()
                        .setCorpId(tpAuthConfig.getCorpId())
                        .setAgentId(tpAuthConfig.getPermanentCode())
                        .setTenantId(tenantId));
            });
        } else {
            cpConfigLoaders.ifAvailable(loader -> {
                config.set(loader.getConfig(tenantId));
            });
        }
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
        String agentId = tenantOperator.getAgentId(tenantId());
        if (agentId != null) {
            return Integer.valueOf(agentId);
        }
        return null;
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
