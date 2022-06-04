package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.CpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

/**
 * 接收企业微信推送消息
 */
@FunctionalInterface
public interface CpListener {

    void listener(String tenantId,
                  WxCpXmlMessage wxMessage,
                  CpService wxCpService);

    CpListener EMPTY = (tenantId, wxMessage, wxCpService) -> {
    };
}
