package com.yj2025.weixin.work.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class WxTpConfig implements Serializable {
    /**
     * 第三方应用ID
     */
    @Nonnull
    private String suiteId;
    /**
     * 第三方应用密钥
     */
    @Nonnull
    private String suiteSecret;
    /**
     * 回调配置 token
     */
    private String token;
    /**
     * 回调配置 aeskey
     */
    private String aesKey;
    private long providerTokenExpiresTime;
    private long suiteAccessTokenExpiresTime;
    private long suiteTicketExpiresTime;
    private String oauth2redirectUri;
}
