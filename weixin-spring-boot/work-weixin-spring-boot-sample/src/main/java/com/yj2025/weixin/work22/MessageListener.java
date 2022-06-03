package com.yj2025.weixin.work22;

import com.google.gson.Gson;
import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpService;
import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.provider.CpListener;
import com.yj2025.weixin.work.provider.TpListener;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yj2025.weixin.work.support.ColorOutput.BLUE;
import static com.yj2025.weixin.work.support.ColorOutput.MAGENTA;

@Slf4j
@Component
public class MessageListener implements CpListener, TpListener {

    @Autowired
    private WxProperties properties;

    private Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void listener(String tenantId, WxCpXmlMessage wxMessage, CpService wxCpService) {
        log.info("tenatnId: {} wxMessage: \n{}", BLUE(tenantId), MAGENTA(gson.toJson(wxMessage)));
    }

    @Override
    public void listener(WxCpTpXmlMessage wxMessage, TpService tpService) {
        log.info("wxMessage: \n{}", MAGENTA(gson.toJson(wxMessage)));

        if (wxMessage.getInfoType() != null) {
            switch (wxMessage.getInfoType()) {
                case "suite_ticket":
                    // https://developer.work.weixin.qq.com/document/path/90628
                    // https://developer.work.weixin.qq.com/document/path/90600
                    tpService.setSuiteTicket(wxMessage.getSuiteTicket(), properties.getTpConfig().getSuiteTicketExpiresTime());
                    break;
            }
        }

    }
}
