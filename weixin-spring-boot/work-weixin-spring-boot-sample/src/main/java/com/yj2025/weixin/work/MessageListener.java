package com.yj2025.weixin.work;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener implements WorkWeixinCpListener {

    private Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void listener(String tenantId, WxCpXmlMessage wxMessage, TenantWxCpService wxCpService) {
        log.info("tenatnId: {}", tenantId);
        log.info("wxMessage: {}", gson.toJson(wxMessage));
        log.info("wxCpService: {}", wxCpService);
    }
}
