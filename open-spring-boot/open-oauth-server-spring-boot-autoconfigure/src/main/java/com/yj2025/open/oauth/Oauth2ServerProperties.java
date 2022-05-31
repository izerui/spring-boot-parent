package com.yj2025.open.oauth;

import com.yj2025.open.commons.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Data
@ConfigurationProperties(prefix = "open.oauth")
public class Oauth2ServerProperties {

    private int accessTokenValiditySeconds = 7200;
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private String alias = Constants.JWT_SSL_ALIAS_FILENAME;
        private Resource caFile = new ClassPathResource(Constants.JWT_SSL_FILENAME);
        private String password = Constants.JWT_SSL_PASSWORD;

    }
}
