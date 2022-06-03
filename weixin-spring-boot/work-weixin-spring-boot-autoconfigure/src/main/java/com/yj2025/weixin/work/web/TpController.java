package com.yj2025.weixin.work.web;

import com.yj2025.weixin.work.config.TpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.message.WxCpTpMessageRouter;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.util.crypto.WxCpTpCryptUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class TpController implements CommandLineRunner {

    @Autowired
    private WxCpTpService tpService;
    @Autowired
    private WxCpTpMessageRouter tpMessageRouter;
    @Autowired
    private TpConfig.JsSdkVerify jsSdkVerify;

    /**
     * 读取配置暴露 可信域名配置地址
     *
     * @return
     */
    @GetMapping("#{jsSdkVerify.verifyTxtPath}")
    public String verifyTxtPath() {
        return jsSdkVerify.getVerifyContent();
    }

    @RequestMapping(value = "/message", produces = "text/html;charset=utf-8")
    public String message(@RequestParam("msg_signature") String msgSignature,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam(value = "echostr", required = false) String echostr,
                          HttpServletRequest request) {
        try {
            // 获取切换后的配置存储对象
            WxCpTpConfigStorage tpConfigStorage = tpService.getWxCpTpConfigStorage();
            WxCpTpCryptUtil cryptUtil = new WxCpTpCryptUtil(tpConfigStorage);
            if (StringUtils.isNotBlank(echostr)) {
                if (!this.tpService.checkSignature(msgSignature, timestamp, nonce, echostr)) {
                    // 消息签名不正确，说明不是公众平台发过来的消息
                    return "非法请求";
                }
                String plainText = cryptUtil.decrypt(echostr);
                // 说明是一个仅仅用来验证的请求，回显echostr
                return plainText;
            }

            Map<String, Object> context = new HashMap<>();

            String plainText = cryptUtil.decryptXml(msgSignature, timestamp, nonce, IOUtils.toString(request.getInputStream()));
            log.debug("解密后的原始xml消息内容：{}", plainText);
            WxCpTpXmlMessage tpXmlMessage = WxCpTpXmlMessage
                    .fromXml(plainText);
            this.tpMessageRouter.route(tpXmlMessage, context);
            return "success";
        } catch (Exception ex) {
            log.error("request: {}", request.getQueryString());
            log.error(ex.getMessage(), ex);
            return "error";
        }
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(":::: tp回调地址: /message");
        log.info(":::: tp可信域名配置地址: " + jsSdkVerify.getVerifyTxtPath());
    }
}
