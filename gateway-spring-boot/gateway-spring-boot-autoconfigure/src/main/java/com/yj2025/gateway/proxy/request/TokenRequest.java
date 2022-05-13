package com.yj2025.gateway.proxy.request;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class TokenRequest {

    private String username;
    private String password;

    public MultiValueMap<String, String> newRequest(String client_id, String client_secret, String grant_type) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.set("client_id", client_id);
        paramMap.set("client_secret", client_secret);
        paramMap.set("grant_type", grant_type);
        paramMap.set("username", username);
        paramMap.set("password", password);
        return paramMap;
    }
}
