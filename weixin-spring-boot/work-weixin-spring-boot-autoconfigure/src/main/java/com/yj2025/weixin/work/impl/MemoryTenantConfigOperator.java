package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantConfigOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;

import java.io.File;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class MemoryTenantConfigOperator extends BaseMemoryTenantOperator implements TenantConfigOperator {

    public MemoryTenantConfigOperator(WorkWeixinProperties properties) {
        super(properties);
    }


    @Override
    public String getTenantIdByAgentId(String agentId) {
        String key = searchKeyByValue(AGENTID_KEY.apply(""), agentId);
        String tenantId = REPLACE_AGENTID_KEY.apply(key);
        return tenantId;
    }

    @Override
    public String getCorpId(String tenantId) {
        return get(CORPID_KEY.apply(tenantId));
    }

    @Override
    public String getCorpSecret(String tenantId) {
        return get(CORPSECRET_KEY.apply(tenantId));
    }

    @Override
    public String getToken(String tenantId) {
        return get(TOKEN_KEY.apply(tenantId));
    }

    @Override
    public String getAesKey(String tenantId) {
        return get(ENCODINGAESKEY_KEY.apply(tenantId));
    }

    @Override
    public Integer getAgentId(String tenantId) {
        String s = get(AGENTID_KEY.apply(tenantId));
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }

    @Override
    public String getMsgAuditLibPath(String tenantId) {
        return get(MSGAUDITLIBPATH_KEY.apply(tenantId));
    }

    @Override
    public void setCorpId(String tenantId, String corpId) {
        set(CORPID_KEY.apply(tenantId), corpId);
    }

    @Override
    public void setCorpSecret(String tenantId, String corpSecret) {
        set(CORPSECRET_KEY.apply(tenantId), corpSecret);
    }

    @Override
    public void setToken(String tenantId, String token) {
        set(TOKEN_KEY.apply(tenantId), token);
    }

    @Override
    public void setAesKey(String tenantId, String encodingAESKey) {
        set(ENCODINGAESKEY_KEY.apply(tenantId), encodingAESKey);
    }

    @Override
    public void setAgentId(String tenantId, String agentId) {
        set(AGENTID_KEY.apply(tenantId), agentId);
    }

    @Override
    public void setMsgAuditLibPath(String tenantId, String msgAuditLibPath) {
        set(MSGAUDITLIBPATH_KEY.apply(tenantId), msgAuditLibPath);
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
    public String getWebhookKey(String tenantId) {
        return get(WEBHOOKKEY_KEY.apply(tenantId));
    }

    @Override
    public void setWebhookKey(String tenantId, String webhookKey) {
        set(WEBHOOKKEY_KEY.apply(tenantId), webhookKey);
    }

    @Override
    public String getOauth2redirectUri(String tenantId) {
        return get(OAUTH2REDIRECTURI_KEY.apply(tenantId));
    }

    @Override
    public void setOauth2redirectUri(String tenantId, String oauth2redirectUri) {
        set(OAUTH2REDIRECTURI_KEY.apply(tenantId), oauth2redirectUri);
    }
}
