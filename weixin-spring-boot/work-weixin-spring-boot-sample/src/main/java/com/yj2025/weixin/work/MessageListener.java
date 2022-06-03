package com.yj2025.weixin.work;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import org.springframework.stereotype.Component;

import static com.yj2025.weixin.work.ColorOutput.*;

@Slf4j
@Component
public class MessageListener implements CpListener, TpListener {

    private Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void listener(String tenantId, WxCpXmlMessage wxMessage, CpService wxCpService) {
        log.info("tenatnId: {} wxMessage: \n{}", BLUE(tenantId), MAGENTA(gson.toJson(wxMessage)));
    }

    @Override
    public void listener(WxCpTpXmlMessage wxMessage, WxCpTpService wxCpTpService) {
        log.info("wxMessage: \n{}", MAGENTA(gson.toJson(wxMessage)));
    }
}
