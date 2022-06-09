package com.yj2025.gateway.proxy.request;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class RefreshRequest {

    private String usercode;
    private String refresh_token;

    public MultiValueMap<String, String> newRequest(String client_id, String client_secret, String grant_type) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.set("client_id", client_id);
        paramMap.set("client_secret", client_secret);
        paramMap.set("grant_type", grant_type);
        paramMap.set("refresh_token", refresh_token);
        paramMap.set("usercode", usercode);
        return paramMap;
    }
}
