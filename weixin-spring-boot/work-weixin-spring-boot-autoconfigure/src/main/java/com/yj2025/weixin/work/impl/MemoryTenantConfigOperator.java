package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantConfig;
import com.yj2025.weixin.work.TenantConfigOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class MemoryTenantConfigOperator extends BaseMemoryTenantOperator implements TenantConfigOperator {

    public MemoryTenantConfigOperator(WorkWeixinProperties properties) {
        super(properties);
    }


    @Override
    public void setConfigs(TenantConfig... configs) {
        for (TenantConfig config : configs) {
            this.setCorpId(config.getTenantId(), config.getCorpId());
            this.setCorpSecret(config.getTenantId(), config.getCorpSecret());
            this.setToken(config.getTenantId(), config.getToken());
            this.setAesKey(config.getTenantId(), config.getAesKey());
            this.setAgentId(config.getTenantId(), config.getAgentId());
            this.setMsgAuditLibPath(config.getTenantId(), config.getMsgAuditLibPath());
            this.setWebhookKey(config.getTenantId(), config.getWebhookKey());
            this.setOauth2redirectUri(config.getTenantId(), config.getOauth2redirectUri());
        }
    }

    @Override
    public TenantConfig getConfig(String tenantId) {
        return new TenantConfig()
                .setTenantId(tenantId)
                .setCorpId(getCorpId(tenantId))
                .setCorpSecret(getCorpSecret(tenantId))
                .setToken(getToken(tenantId))
                .setAesKey(getAesKey(tenantId))
                .setAgentId(getAgentId(tenantId))
                .setMsgAuditLibPath(getMsgAuditLibPath(tenantId))
                .setWebhookKey(getWebhookKey(tenantId));
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
    public void setAgentId(String tenantId, Integer agentId) {
        set(AGENTID_KEY.apply(tenantId), String.valueOf(agentId));
    }

    @Override
    public void setMsgAuditLibPath(String tenantId, String msgAuditLibPath) {
        set(MSGAUDITLIBPATH_KEY.apply(tenantId), msgAuditLibPath);
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
