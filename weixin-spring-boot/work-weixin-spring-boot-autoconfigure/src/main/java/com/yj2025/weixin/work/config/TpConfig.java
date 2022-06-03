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
public class TpConfig implements Serializable {
    /**
     * 第三方应用ID 来自于企微配置
     */
    @Nonnull
    private String suiteId;
    /**
     * 第三方应用密钥
     */
    @Nonnull
    private String suiteSecret;
    /**
     * 第三方应用的token，用来检查应用的签名
     */
    private String token;
    /**
     * 第三方应用的EncodingAESKey，用来检查签名
     */
    private String aesKey;
    /**
     * 企微服务商 企业ID
     */
    private String corpId;
    /**
     * 企微服务商 企业secret，来自于企微配置
     */
    private String corpSecret;
    /**
     * 服务商secret
     */
    private String providerSecret;
    private long providerTokenExpiresTime;
    private long suiteAccessTokenExpiresTime;
    private long suiteTicketExpiresTime;
    private String oauth2redirectUri;
}
