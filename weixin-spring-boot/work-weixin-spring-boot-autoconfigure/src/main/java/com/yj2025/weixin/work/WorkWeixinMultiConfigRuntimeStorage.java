package com.yj2025.weixin.work;

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
public class WorkWeixinMultiConfigRuntimeStorage implements WxCpConfigStorage {

    private WorkWeixinTenantConfigOperator configOperator;
    private WorkWeixinTenantRuntimeOperator runtimeOperator;
    private ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders;

    public WorkWeixinMultiConfigRuntimeStorage(WorkWeixinTenantConfigOperator configOperator,
                                               WorkWeixinTenantRuntimeOperator runtimeOperator,
                                               ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders) {
        this.configOperator = configOperator;
        this.runtimeOperator = runtimeOperator;
        this.apacheHttpClientBuilders = apacheHttpClientBuilders;
    }

    // 当无法获取对应租户ID的时候使用的默认的租户ID
    public final static String DEFAULT_TENANT_ID = "default";

    // 当前线程使用的tenantId(公司编号)
    private final static InheritableThreadLocal<String> INHERITABLE_THREAD_ACTIVE_TENANT_ID;

    static {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID = new InheritableThreadLocal<>();
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(DEFAULT_TENANT_ID);
    }

    /**
     * 当前请求线程切换使用的租户配置
     *
     * @param tenantId
     * @return
     */
    public WorkWeixinMultiConfigRuntimeStorage tenant(String tenantId) {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(tenantId);
        return this;
    }

    private String tenantId() {
        return INHERITABLE_THREAD_ACTIVE_TENANT_ID.get();
    }

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
        return runtimeOperator.getAccessToken(tenantId());
    }

    @Override
    public Lock getAccessTokenLock() {
        return runtimeOperator.getAccessTokenLock(tenantId());
    }

    @Override
    public boolean isAccessTokenExpired() {
        return runtimeOperator.isAccessTokenExpired(tenantId());
    }

    @Override
    public void expireAccessToken() {
        runtimeOperator.expireAccessToken(tenantId());
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        runtimeOperator.updateAccessToken(tenantId(), accessToken);
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresIn) {
        WxAccessToken wat = new WxAccessToken();
        wat.setAccessToken(accessToken);
        wat.setExpiresIn(expiresIn);
        runtimeOperator.updateAccessToken(tenantId(), wat);
    }

    @Override
    public String getJsapiTicket() {
        return runtimeOperator.getJsapiTicket(tenantId());
    }

    @Override
    public Lock getJsapiTicketLock() {
        return runtimeOperator.getJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return runtimeOperator.isJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireJsapiTicket() {
        runtimeOperator.expireJsapiTicket(tenantId());
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        runtimeOperator.setJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket() {
        return runtimeOperator.getAgentJsapiTicket(tenantId());
    }

    @Override
    public Lock getAgentJsapiTicketLock() {
        return runtimeOperator.getAgentJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isAgentJsapiTicketExpired() {
        return runtimeOperator.isAgentJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireAgentJsapiTicket() {
        runtimeOperator.expireAgentJsapiTicket(tenantId());
    }

    @Override
    public void updateAgentJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        runtimeOperator.updateAgentJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getCorpId() {
        return configOperator.getCorpId(tenantId());
    }

    @Override
    public String getCorpSecret() {
        return configOperator.getCorpSecret(tenantId());
    }

    @Override
    public Integer getAgentId() {
        return configOperator.getAgentId(tenantId());
    }

    @Override
    public String getToken() {
        return configOperator.getToken(tenantId());
    }

    @Override
    public String getAesKey() {
        return configOperator.getAesKey(tenantId());
    }

    @Override
    public String getMsgAuditLibPath() {
        return configOperator.getMsgAuditLibPath(tenantId());
    }

    @Override
    public long getExpiresTime() {
        return runtimeOperator.getExpiresTime(tenantId());
    }

    @Override
    public String getOauth2redirectUri() {
        return configOperator.getOauth2redirectUri(tenantId());
    }

    @Override
    public String getHttpProxyHost() {
        return configOperator.getHttpProxyHost();
    }

    @Override
    public int getHttpProxyPort() {
        return configOperator.getHttpProxyPort();
    }

    @Override
    public String getHttpProxyUsername() {
        return configOperator.getHttpProxyUsername();
    }

    @Override
    public String getHttpProxyPassword() {
        return configOperator.getHttpProxyPassword();
    }

    @Override
    public File getTmpDirFile() {
        return configOperator.getTmpDirFile();
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
        return configOperator.getWebhookKey(tenantId());
    }
}
