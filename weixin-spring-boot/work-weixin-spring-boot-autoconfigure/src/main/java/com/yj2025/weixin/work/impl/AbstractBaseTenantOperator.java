package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantConfig;
import com.yj2025.weixin.work.TenantConfigOperator;
import com.yj2025.weixin.work.TenantRuntimeOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;
import me.chanjar.weixin.common.bean.WxAccessToken;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public abstract class AbstractBaseTenantOperator implements TenantConfigOperator, TenantRuntimeOperator, KeyConstants {

    protected WorkWeixinProperties properties;

    public AbstractBaseTenantOperator(WorkWeixinProperties properties) {
        this.properties = properties;
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    protected abstract String get(String key);

    /**
     * 持久化一个kv
     *
     * @param key
     * @param value
     */
    protected abstract void set(String key, String value);

    /**
     * 持久化kv并设置失效时长（秒）
     *
     * @param key
     * @param value
     * @param expiredSeconds
     */
    protected abstract void set(String key, String value, int expiredSeconds);

    /**
     * 移除一个key
     *
     * @param key
     */
    protected abstract void remove(String key);

    /**
     * 判断一个key是否存在
     *
     * @param key
     * @return
     */
    protected abstract boolean exist(String key);

    /**
     * 获取一个key的到期时长
     *
     * @param key
     * @return
     */
    protected abstract long getExpiredSeconds(String key);

    // config
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


    // runtime
    @Override
    public boolean isAgentJsapiTicketExpired(String tenantId) {
        return !exist(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireAgentJsapiTicket(String tenantId) {
        remove(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(AGENTJSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public void updateAccessToken(String tenantId, WxAccessToken accessToken) {
        set(ACCESSTOKEN_KEY.apply(tenantId), accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    @Override
    public String getAccessToken(String tenantId) {
        return get(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public long getExpiresTime(String tenantId) {
        return getExpiredSeconds(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public boolean isJsapiTicketExpired(String tenantId) {
        return !exist(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireJsapiTicket(String tenantId) {
        remove(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public String getJsapiTicket(String tenantId) {
        return get(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(JSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket(String tenantId) {
        return get(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public boolean isAccessTokenExpired(String tenantId) {
        return !exist(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public void expireAccessToken(String tenantId) {
        remove(ACCESSTOKEN_KEY.apply(tenantId));
    }

}
