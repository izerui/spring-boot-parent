package com.yj2025.oauth2.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@ConfigurationProperties(prefix = "oauth2.server")
public class Oauth2Properties {

    /**
     * 支持的客户端id
     */
    private String clientId = "ierp";
    /**
     * 客户端密钥
     */
    private String clientSecret = "123456";
    /**
     * 重定向url
     */
    private String redirectUri = "https://yj2025.com";
    /**
     * access_token 失效时长（秒）
     */
    private int accessTokenValiditySeconds = 7200;
    /**
     * refresh_token 失效时长（秒）
     */
    private int refreshTokenValiditySeconds = 86400;
    /**
     * 是否支持通过url参数传递access_token来获取用户身份
     */
    private boolean allowUriToken = true;
    /**
     * jwt 支持
     */
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        /**
         * 是否开启jwt支持
         */
        private boolean enabled = false;
        /**
         * jwt证书
         */
        private Resource keyFile;
        /**
         * jwt证书别名
         */
        private String keyAlias;
        /**
         * jwt证书密码
         */
        private String keyPassword;
    }

}
