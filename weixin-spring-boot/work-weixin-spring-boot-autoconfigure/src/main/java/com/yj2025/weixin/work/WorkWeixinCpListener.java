package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

public interface WorkWeixinCpListener {

    void listener(String tenantId,
                  WxCpXmlMessage wxMessage,
                  TenantWxCpService wxCpService);

    WorkWeixinCpListener EMPTY = (tenantId, wxMessage, wxCpService) -> {
    };
}
