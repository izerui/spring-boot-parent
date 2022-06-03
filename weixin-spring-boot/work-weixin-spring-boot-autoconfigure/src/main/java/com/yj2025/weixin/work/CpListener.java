package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

public interface CpListener {

    void listener(String tenantId,
                  WxCpXmlMessage wxMessage,
                  CpService wxCpService);

    CpListener EMPTY = (tenantId, wxMessage, wxCpService) -> {
    };
}
