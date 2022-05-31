package com.yj2025.open.oauth2;

import com.yj2025.open.commons.ClientStore;
import com.yj2025.open.oauth.provider.AbstractClientProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuyuhua
 */
@Component
public class DefaultClientProvider extends AbstractClientProvider {

    public DefaultClientProvider(@Autowired ClientStore clientStore) {
        super(clientStore);
    }

    @Override
    protected String doGetClientSecret(String clientId) {
        if(clientId.equals("ierp-gateway")) {
            return "secret001";
        }
        throw new RuntimeException("错误的密钥");
    }

    @Override
    protected String doGetTenantId(String clientId) {
        return "ent001";
    }
}
