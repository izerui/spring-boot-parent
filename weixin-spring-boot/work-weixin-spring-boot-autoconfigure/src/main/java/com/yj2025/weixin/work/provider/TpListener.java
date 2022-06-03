package com.yj2025.weixin.work.provider;

import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;

public interface TpListener {

    void listener(WxCpTpXmlMessage wxMessage,
                  WxCpTpService wxCpTpService);

    TpListener EMPTY = (wxMessage, wxCpService) -> {
    };
}
