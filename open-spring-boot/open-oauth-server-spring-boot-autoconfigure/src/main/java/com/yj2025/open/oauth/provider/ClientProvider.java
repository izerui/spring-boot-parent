package com.yj2025.open.oauth.provider;

/**
 * @author liuyuhua
 */
public interface ClientProvider {
    /**
     * 根据客户端ID获取密钥等配置
     *
     * @param clientId
     * @return
     */
    String getClientSecret(String clientId);

    /**
     * 客户端ID获取所属的租户ID
     *
     * @param clientId
     * @return
     */
    String getTenantId(String clientId);
}
