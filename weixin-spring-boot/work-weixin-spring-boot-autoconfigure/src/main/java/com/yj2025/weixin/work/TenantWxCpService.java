package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.api.WxCpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface TenantWxCpService extends WxCpService {
    TenantWxCpService tenant(String tenantId);
    String getTenantIdByAgentId(String agentId);
}
