package com.yj2025.weixin.work;

import java.io.File;

/**
 * 租户相关配置的获取及更新操作类
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface TenantConfigOperator {

    /**
     * 根据消息相应的agentId获取对应的tenantId
     * @param agentId
     * @return
     */
    String getTenantIdByAgentId(String agentId);

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
    void setAesKey(String tenantId, String encodingAESKey);

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

}
