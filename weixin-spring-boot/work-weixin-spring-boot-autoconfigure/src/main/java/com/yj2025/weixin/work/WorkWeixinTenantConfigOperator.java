package com.yj2025.weixin.work;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 租户相关配置的获取及更新操作类
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface WorkWeixinTenantConfigOperator {

    /**
     * 微信企业号 corpId
     *
     * @param tenantId
     * @return
     */
    String getCorpId(String tenantId);

    /**
     * 微信企业号 corpSecret
     */
    String getCorpSecret(String tenantId);

    /**
     * 微信企业号应用 token
     */
    String getToken(String tenantId);

    /**
     * 微信企业号应用 EncodingAESKey
     */
    String getAesKey(String tenantId);

    /**
     * 微信企业号应用 ID
     */
    Integer getAgentId(String tenantId);

    /**
     * 微信企业号应用 会话存档类库路径
     */
    String getMsgAuditLibPath(String tenantId);

    /**
     * 微信企业号 corpId
     */
    void setCorpId(String tenantId, String corpId);

    /**
     * 微信企业号 corpSecret
     */
    void setCorpSecret(String tenantId, String corpSecret);

    /**
     * 微信企业号应用 token
     */
    void setToken(String tenantId, String token);

    /**
     * 微信企业号应用 EncodingAESKey
     */
    void setAesKey(String tenantId, String EncodingAESKey);

    /**
     * 微信企业号应用 ID
     */
    void setAgentId(String tenantId, String agentId);

    /**
     * 微信企业号应用 会话存档类库路径
     */
    void setMsgAuditLibPath(String tenantId, String msgAuditLibPath);

    String getHttpProxyHost();

    int getHttpProxyPort();

    String getHttpProxyUsername();

    String getHttpProxyPassword();

    File getTmpDirFile();

    String getWebhookKey(String tenantId);

    void setWebhookKey(String tenantId, String webhookKey);

    String getOauth2redirectUri(String tenantId);

    void setOauth2redirectUri(String tenantId, String oauth2redirectUri);

    class Default implements WorkWeixinTenantConfigOperator, IWorkWeixinConstant {

        private StringRedisTemplate redisTemplate;
        private WorkWeixinProperties properties;

        public Default(StringRedisTemplate redisTemplate, WorkWeixinProperties properties) {
            this.redisTemplate = redisTemplate;
            this.properties = properties;
        }

        @Override
        public String getCorpId(String tenantId) {
            return redisTemplate.boundValueOps($_(CORPID_KEY, tenantId)).get();
        }

        @Override
        public String getCorpSecret(String tenantId) {
            return redisTemplate.boundValueOps($_(CORPSECRET_KEY, tenantId)).get();
        }

        @Override
        public String getToken(String tenantId) {
            return redisTemplate.boundValueOps($_(TOKEN_KEY, tenantId)).get();
        }

        @Override
        public String getAesKey(String tenantId) {
            return redisTemplate.boundValueOps($_(ENCODINGAESKEY_KEY, tenantId)).get();
        }

        @Override
        public Integer getAgentId(String tenantId) {
            String s = redisTemplate.boundValueOps($_(AGENTID_KEY, tenantId)).get();
            if (StringUtils.isNotBlank(s)) {
                return Integer.valueOf(s);
            }
            return null;
        }

        @Override
        public String getMsgAuditLibPath(String tenantId) {
            return redisTemplate.boundValueOps($_(MSGAUDITLIBPATH_KEY, tenantId)).get();
        }

        @Override
        public void setCorpId(String tenantId, String corpId) {
            redisTemplate.boundValueOps($_(CORPID_KEY, tenantId)).set(corpId);
        }

        @Override
        public void setCorpSecret(String tenantId, String corpSecret) {
            redisTemplate.boundValueOps($_(CORPSECRET_KEY, tenantId)).set(corpSecret);
        }

        @Override
        public void setToken(String tenantId, String token) {
            redisTemplate.boundValueOps($_(TOKEN_KEY, tenantId)).set(token, 7000, TimeUnit.SECONDS);
        }

        @Override
        public void setAesKey(String tenantId, String encodingAESKey) {
            redisTemplate.boundValueOps($_(ENCODINGAESKEY_KEY, tenantId)).set(encodingAESKey);
        }

        @Override
        public void setAgentId(String tenantId, String agentId) {
            redisTemplate.boundValueOps($_(AGENTID_KEY, tenantId)).set(agentId);
        }

        @Override
        public void setMsgAuditLibPath(String tenantId, String msgAuditLibPath) {
            redisTemplate.boundValueOps($_(MSGAUDITLIBPATH_KEY, tenantId)).set(msgAuditLibPath);
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
            return redisTemplate.boundValueOps($_(WEBHOOKKEY_KEY, tenantId)).get();
        }

        @Override
        public void setWebhookKey(String tenantId, String webhookKey) {
            redisTemplate.boundValueOps($_(WEBHOOKKEY_KEY, tenantId)).set(webhookKey);
        }

        @Override
        public String getOauth2redirectUri(String tenantId) {
            return redisTemplate.boundValueOps($_(OAUTH2REDIRECTURI_KEY, tenantId)).get();
        }

        @Override
        public void setOauth2redirectUri(String tenantId, String oauth2redirectUri) {
            redisTemplate.boundValueOps($_(OAUTH2REDIRECTURI_KEY, tenantId)).set(oauth2redirectUri);
        }

    }
}
