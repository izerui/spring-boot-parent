package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;

/**
 * 监听企业微信推送的服务商消息
 */
@FunctionalInterface
public interface TpListener {

    void listener(WxCpTpXmlMessage wxMessage,
                  TpService tpService);

    TpListener EMPTY = (wxMessage, wxCpService) -> {
    };
}
