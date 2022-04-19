package com.yj2025.weixin.work;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
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
