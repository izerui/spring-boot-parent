package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.api.WxCpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface WorkWeixinService extends WxCpService {
    WorkWeixinService tenant(String tenantId);
}
