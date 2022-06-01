package com.yj2025.open.oauth.security;

import com.yj2025.open.commons.Constants;
import com.yj2025.open.oauth.provider.ClientProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT内容增强器 添加租户ID（账套编号）
 *
 * @author liuyuhua
 */
public class JwtTokenEnhancer implements TokenEnhancer {

    private ObjectProvider<ClientProvider> clientProviders;

    public JwtTokenEnhancer(ObjectProvider<ClientProvider> clientProviders) {
        this.clientProviders = clientProviders;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        ClientProvider provider = this.clientProviders.getIfAvailable();
        Assert.notNull(provider, "必须存在一个类型为 com.yj2025.open.oauth.provider.ClientProvider 的Bean");
        String clientId = authentication.getOAuth2Request().getClientId();
        String tenantId = provider.getTenantId(clientId);
        Assert.notNull(tenantId, "未找到 " + clientId + " 所属的账套");
        Map<String, Object> info = new HashMap<>(1);
        info.put(Constants.TENANT_ID_FIELDNAME, tenantId);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }

}
