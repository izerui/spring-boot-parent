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
     * suiteId 服务商平台第三方应用页面查看
     */
    @Nonnull
    private String suiteId;
    /**
     * 第三方应用密钥 服务商平台-应用管理页面查看
     */
    @Nonnull
    private String suiteSecret;
    /**
     * 企微服务商企业ID 服务商平台-服务商信息页面查看
     */
    private String corpId;
    /**
     * 企微服务商 企业secret，来自于企微配置 (姑且可以获取)
     */
    private String corpSecret;
    /**
     * 第三方应用的token，用来检查应用的签名 服务商平台-应用管理页面-回调配置栏查看
     */
    private String listenerToken;
    /**
     * 第三方应用的EncodingAESKey，用来检查签名 服务商平台-应用管理页面-回调配置栏查看
     */
    private String listenerAesKey;
    /**
     * 服务商secret
     */
    private String providerSecret;
    private JsSdkVerify jsSdkVerify = new JsSdkVerify();
    private int providerTokenExpiresTime = 7200;
    private int suiteAccessTokenExpiresTime = 7200;
    private int suiteTicketExpiresTime = 1680; // 28分钟过期
    private String oauth2redirectUri;

    @Data
    public static class JsSdkVerify {
        private String verifyTxtPath = "/WW_verify_Rd0su22ZohsSXlGI.txt";
        private String verifyContent = "Rd0su22ZohsSXlGI";
    }
}
