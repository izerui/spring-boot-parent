package com.yj2025.open.commons;

/**
 * @author liuyuhua
 */
public interface ClientStore {
    /**
     * 保存当前的clientId和clientSecret
     *
     * @param clientId
     * @param clientSecret
     */
    void saveClientSecret(String clientId, String clientSecret);

    /**
     * 根据clientId获取密钥
     *
     * @param clientId
     * @return
     */
    String getClientSecret(String clientId);
}
