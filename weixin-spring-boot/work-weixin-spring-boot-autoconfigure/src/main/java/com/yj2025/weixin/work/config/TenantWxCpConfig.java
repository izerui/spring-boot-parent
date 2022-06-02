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
public class TenantWxCpConfig implements Serializable {
    @Nonnull
    private String tenantId;
    @Nonnull
    private String corpId;
    @Nonnull
    private String corpSecret;
    private String token;
    private String aesKey;
    @Nonnull
    private Integer agentId;
    private String msgAuditLibPath;
    private String oauth2redirectUri;
    private String webhookKey;
}
