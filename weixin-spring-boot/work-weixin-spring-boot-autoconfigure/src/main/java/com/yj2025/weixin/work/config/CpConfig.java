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
public class CpConfig implements Serializable {
    @Nonnull
    private String tenantId;
    /**
     * 企业ID 我的企业页面查看
     */
    @Nonnull
    private String corpId;
    /**
     * 应用密钥 我的应用页面查看
     */
    @Nonnull
    private String corpSecret;
    /**
     * 应用id 我的应用页面查看
     */
    @Nonnull
    private Integer agentId;
    /**
     * 回调token 启用api接收页面查看
     */
    private String listenerToken;
    /**
     * 回调aeskey 启用api接收页面查看
     */
    private String listenerAesKey;
    private String msgAuditLibPath;
    private String oauth2redirectUri;
    private String webhookKey;
}
