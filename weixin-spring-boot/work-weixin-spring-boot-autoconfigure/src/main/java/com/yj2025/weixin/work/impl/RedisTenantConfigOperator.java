package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantConfigOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class RedisTenantConfigOperator implements TenantConfigOperator, KeyConstants {

    private StringRedisTemplate redisTemplate;
    private WorkWeixinProperties properties;

    public RedisTenantConfigOperator(StringRedisTemplate redisTemplate, WorkWeixinProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public String getTenantIdByAgentId(String agentId) {
        String pattern = AGENTID_KEY.apply("*");
        Set<String> keys = redisTemplate.keys(pattern);
        for (String key : keys) {
            String value = redisTemplate.boundValueOps(key).get();
            if(StringUtils.equals(value,agentId)) {
                return REPLACE_AGENTID_KEY.apply(key);
            }
        }
        return null;
    }

    @Override
    public String getCorpId(String tenantId) {
        return redisTemplate.boundValueOps(CORPID_KEY.apply(tenantId)).get();
    }

    @Override
    public String getCorpSecret(String tenantId) {
        return redisTemplate.boundValueOps(CORPSECRET_KEY.apply(tenantId)).get();
    }

    @Override
    public String getToken(String tenantId) {
        return redisTemplate.boundValueOps(TOKEN_KEY.apply(tenantId)).get();
    }

    @Override
    public String getAesKey(String tenantId) {
        return redisTemplate.boundValueOps(ENCODINGAESKEY_KEY.apply(tenantId)).get();
    }

    @Override
    public Integer getAgentId(String tenantId) {
        String s = redisTemplate.boundValueOps(AGENTID_KEY.apply(tenantId)).get();
        if (StringUtils.isNotBlank(s)) {
            return Integer.valueOf(s);
        }
        return null;
    }

    @Override
    public String getMsgAuditLibPath(String tenantId) {
        return redisTemplate.boundValueOps(MSGAUDITLIBPATH_KEY.apply(tenantId)).get();
    }

    @Override
    public void setCorpId(String tenantId, String corpId) {
        redisTemplate.boundValueOps(CORPID_KEY.apply(tenantId)).set(corpId);
    }

    @Override
    public void setCorpSecret(String tenantId, String corpSecret) {
        redisTemplate.boundValueOps(CORPSECRET_KEY.apply(tenantId)).set(corpSecret);
    }

    @Override
    public void setToken(String tenantId, String token) {
        redisTemplate.boundValueOps(TOKEN_KEY.apply(tenantId)).set(token, 7000, TimeUnit.SECONDS);
    }

    @Override
    public void setAesKey(String tenantId, String encodingAESKey) {
        redisTemplate.boundValueOps(ENCODINGAESKEY_KEY.apply(tenantId)).set(encodingAESKey);
    }

    @Override
    public void setAgentId(String tenantId, String agentId) {
        redisTemplate.boundValueOps(AGENTID_KEY.apply(tenantId)).set(agentId);
    }

    @Override
    public void setMsgAuditLibPath(String tenantId, String msgAuditLibPath) {
        redisTemplate.boundValueOps(MSGAUDITLIBPATH_KEY.apply(tenantId)).set(msgAuditLibPath);
    }

    @Override
    public String getWebhookKey(String tenantId) {
        return redisTemplate.boundValueOps(WEBHOOKKEY_KEY.apply(tenantId)).get();
    }

    @Override
    public void setWebhookKey(String tenantId, String webhookKey) {
        redisTemplate.boundValueOps(WEBHOOKKEY_KEY.apply(tenantId)).set(webhookKey);
    }

    @Override
    public String getOauth2redirectUri(String tenantId) {
        return redisTemplate.boundValueOps(OAUTH2REDIRECTURI_KEY.apply(tenantId)).get();
    }

    @Override
    public void setOauth2redirectUri(String tenantId, String oauth2redirectUri) {
        redisTemplate.boundValueOps(OAUTH2REDIRECTURI_KEY.apply(tenantId)).set(oauth2redirectUri);
    }
}
