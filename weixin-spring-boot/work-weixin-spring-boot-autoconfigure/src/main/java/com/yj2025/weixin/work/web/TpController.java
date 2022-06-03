package com.yj2025.weixin.work.web;

import com.yj2025.weixin.work.WxProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.yj2025.weixin.work.support.ColorOutput.*;

@Slf4j
@RestController
public class TpController implements CommandLineRunner {

    @Autowired
    private WxCpTpService tpService;
    @Autowired
    private WxCpTpMessageRouter tpMessageRouter;
    @Autowired
    private WxProperties.TpConfig.JsSdkVerify jsSdkVerify;
    @Autowired
    private WxProperties properties;

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

    /**
     * 第三方应用安装完毕回调地址
     *
     * @return
     */
    @GetMapping("/")
    public void home(@RequestParam("auth_code") String authCode,
                     @RequestParam("expires_in") String expiresIn,
                     @RequestParam("state") String state,
                     HttpServletResponse response) throws WxErrorException, IOException {
        WxCpTpPermanentCodeInfo info = tpService.getPermanentCodeInfo(authCode);
        log.info(info.toJson());
        log.info(GREEN("\n永久授权码: {} \n授权人: {} \ncorpid: {} \nagentid: {} \naccess_token: {}"), info.getPermanentCode(), info.getAuthUserInfo().getUserId(), info.getAuthCorpInfo().getCorpId(), info.getAuthInfo().getAgents().get(0).getAgentId(), info.getAccessToken());
        response.sendRedirect(properties.getTpConfig().getPermanentCodeRedirectUri());
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(BLUE(":::: 第三方应用安装回调地址: /"));
        log.info(BLUE(":::: 第三方应用回调地址: /message"));
        log.info(BLUE(":::: 第三方应用可信域名配置地址: " + jsSdkVerify.getVerifyTxtPath()));
    }
}
