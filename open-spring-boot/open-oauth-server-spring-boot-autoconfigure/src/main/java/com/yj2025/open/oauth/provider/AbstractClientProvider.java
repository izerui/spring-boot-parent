package com.yj2025.open.oauth.provider;

import com.yj2025.open.commons.ClientStore;

/**
 * 需要自定义实现的client相关信息获取的基类，继承该抽象类并实现里面的抽象方法
 *
 * @author liuyuhua
 */
public abstract class AbstractClientProvider implements ClientProvider {

    private final ClientStore clientStore;

    public AbstractClientProvider(ClientStore clientStore) {
        this.clientStore = clientStore;
    }

    @Override
    public final String getClientSecret(String clientId) {
        String clientSecret = doGetClientSecret(clientId);
        // 缓存最新的secret，网关需要通过缓存获取
        clientStore.saveClientSecret(clientId, clientSecret);
        return clientSecret;
    }

    @Override
    public final String getTenantId(String clientId) {
        return doGetTenantId(clientId);
    }

    /**
     * 根据clientId获取密钥
     *
     * @param clientId
     * @return
     */
    protected abstract String doGetClientSecret(String clientId);

    /**
     * 根据clientId获取租户ID
     *
     * @param clientId
     * @return
     */
    protected abstract String doGetTenantId(String clientId);

}
