package com.yj2025.weixin.work.listener;

import com.yj2025.weixin.work.TenantWxCpService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class WxCpListenerController implements CommandLineRunner {

    @Autowired
    private TenantWxCpService tenantWxCpService;
    @Autowired
    private WxCpMessageRouter wxCpMessageRouter;
    @Autowired
    private ApplicationContext applicationContext;


    @GetMapping("/get")
    public String messge2() {
        return "success";
    }

    @RequestMapping(value = "/message/{tenantId}", produces = "text/html;charset=utf-8")
    public String message(@PathVariable("tenantId") String tenantId,
                          @RequestParam("msg_signature") String msgSignature,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam(value = "echostr", required = false) String echostr,
                          HttpServletRequest request) throws IOException {
        try {
            // 必要: 切换配置
            tenantWxCpService.tenant(tenantId);
            // 获取切换后的配置存储对象
            WxCpConfigStorage wxCpConfigStorage = tenantWxCpService.getWxCpConfigStorage();
            if (StringUtils.isNotBlank(echostr)) {
                if (!this.tenantWxCpService.checkSignature(msgSignature, timestamp, nonce, echostr)) {
                    // 消息签名不正确，说明不是公众平台发过来的消息
                    return "非法请求";
                }
                WxCpCryptUtil cryptUtil = new WxCpCryptUtil(wxCpConfigStorage);
                String plainText = cryptUtil.decrypt(echostr);
                // 说明是一个仅仅用来验证的请求，回显echostr
                return plainText;
            }

            Map<String, Object> context = new HashMap<>();
            context.put("tenantId", tenantId);

            WxCpXmlMessage inMessage = WxCpXmlMessage
                    .fromEncryptedXml(request.getInputStream(), wxCpConfigStorage, timestamp, nonce, msgSignature);
            WxCpXmlOutMessage outMessage = this.wxCpMessageRouter.route(inMessage, context);
            if (outMessage != null) {
                return outMessage.toEncryptedXml(wxCpConfigStorage);
            }
            return "非法请求";
        } catch (Exception ex) {
            log.error("request: {}", request.getQueryString());
            log.error(ex.getMessage(), ex);
            return "非法请求";
        }
    }


    private final static String LABEL_LINE_RUNNER = ":::: 开始监听企业微信自建应用消息回调,回调地址: http://{}:{}{}/message/{tenantId}";

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
