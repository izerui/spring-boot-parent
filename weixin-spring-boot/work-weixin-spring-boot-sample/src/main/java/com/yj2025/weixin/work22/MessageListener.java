package com.yj2025.weixin.work22;

import com.google.gson.Gson;
import com.yj2025.weixin.work.CpListener;
import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpListener;
import com.yj2025.weixin.work.WxProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yj2025.weixin.work22.ColorOutput.BLUE;
import static com.yj2025.weixin.work22.ColorOutput.MAGENTA;

@Slf4j
@Component
public class MessageListener implements CpListener, TpListener {

    @Autowired
    private WxCpTpService tpService;
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
    public void listener(WxCpTpXmlMessage wxMessage, WxCpTpService wxCpTpService) {
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
