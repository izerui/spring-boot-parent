package com.yj2025.weixin.work.provider;

import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;

public interface TpListener {

    void listener(WxCpTpXmlMessage wxMessage,
                  TpService tpService);

    TpListener EMPTY = (wxMessage, wxCpService) -> {
    };
}
