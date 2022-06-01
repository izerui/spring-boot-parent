package com.yj2025.open.oauth.security;

import com.yj2025.open.commons.Constants;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author liuyuhua
 */
public class ClientDefination extends BaseClientDetails {

    public ClientDefination(String clientId, String clientSecret) {
        super(clientId, null, Constants.ACCESS_TOKEN_SCOPES, Constants.ACCESS_TOKEN_GRANTTYPES, null);
        this.setClientSecret(clientSecret);
        this.setAccessTokenValiditySeconds(Constants.ACCESS_TOKEN_VALIDITY_SECONDS);
    }

}
