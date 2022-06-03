package com.yj2025.weixin.work.listener;

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
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class TpListenerController implements CommandLineRunner {

    @Autowired
    private WxCpTpService tpService;
    @Autowired
    private WxCpTpMessageRouter tpMessageRouter;
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/message", produces = "text/html;charset=utf-8")
    public String message(@RequestParam("msg_signature") String msgSignature,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam(value = "echostr", required = false) String echostr,
                          HttpServletRequest request) {
        try {
            // 获取切换后的配置存储对象
            WxCpTpConfigStorage tpConfigStorage = tpService.getWxCpTpConfigStorage();
            if (StringUtils.isNotBlank(echostr)) {
                if (!this.tpService.checkSignature(msgSignature, timestamp, nonce, echostr)) {
                    // 消息签名不正确，说明不是公众平台发过来的消息
                    return "非法请求";
                }
                WxCpTpCryptUtil cryptUtil = new WxCpTpCryptUtil(tpConfigStorage);
                String plainText = cryptUtil.decrypt(echostr);
                // 说明是一个仅仅用来验证的请求，回显echostr
                return plainText;
            }

            Map<String, Object> context = new HashMap<>();

            WxCpTpXmlMessage tpXmlMessage = WxCpTpXmlMessage
                    .fromXml(IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8));
            this.tpMessageRouter.route(tpXmlMessage, context);
            return "success";
        } catch (Exception ex) {
            log.error("request: {}", request.getQueryString());
            log.error(ex.getMessage(), ex);
            return "error";
        }
    }


    private final static String LABEL_LINE_RUNNER = ":::: 开始监听企业微信第三方应用消息回调,回调地址: http://{}:{}{}/message}";

    @Override
    public void run(String... args) throws Exception {
        Environment env = applicationContext.getEnvironment();
        log.info(LABEL_LINE_RUNNER,
                InetAddress.getLocalHost().getHostAddress(),
                Optional.ofNullable(env.getProperty("server.port")).orElse("8080"),
                Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("")
        );
    }
}
