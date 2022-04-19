package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.api.WxCpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface TenantWxCpService extends WxCpService {
    /**
     * 指定以某一个租户操作service请求
     * @param tenantId
     * @return
     */
    TenantWxCpService tenant(String tenantId);

    /**
     * 根据agentId获取对应的租户
     * @param agentId
     * @return
     */
    String getTenantIdByAgentId(String agentId);
}
