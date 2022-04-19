package com.yj2025.weixin.work;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TenantConfig {
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
