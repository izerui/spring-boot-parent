package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.TenantWxCpConfig;
import com.yj2025.weixin.work.config.TenantWxCpConfigOperator;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.bean.templatecard.HorizontalContent;
import me.chanjar.weixin.cp.bean.templatecard.TemplateCardJump;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
@SpringBootTest(classes = Application.class)
public class TenantTests {

    @Autowired
    private TenantWxCpService tenantWxCpService;


    private void sendDemoMessage(String tenantId) throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = tenantWxCpService.tenant(tenantId).getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }

    /**
     * 测试读取spring 配置的多租户信息进行发送测试
     *
     * @throws WxErrorException
     */
    @Test
    public void testSpring() throws WxErrorException {
        this.sendDemoMessage("k8s-local");
    }

    @Test
    public void testTemplateMessage() throws WxErrorException {
        WxCpMessage reply = WxCpMessage.TEMPLATECARD().toUser("serv")
                .cardType(WxConsts.TemplateCardType.TEXT_NOTICE)
                .sourceIconUrl("http://www.yunji2025.com/_nuxt/img/logo.27aea34.png")
                .sourceDesc("服务发布")
                .mainTitleTitle("admin-server 发布成功")
                .mainTitleDesc(new Date().toString())
                .horizontalContents(Arrays.asList(
                        HorizontalContent.builder()
                                .keyname("集群")
                                .value("local")
                                .build(),
                        HorizontalContent.builder()
                                .keyname("环境")
                                .value("test")
                                .build(),
                        HorizontalContent.builder()
                                .keyname("镜像版本")
                                .value("UIHJDNSFIU")
                                .build()))
                .jumps(List.of(
                        TemplateCardJump.builder()
                                .type(1)
                                .title("进入我的的经管")
                                .url("https://yj2025.com")
                                .build()
                ))
                .cardActionType(1)
                .cardActionUrl("https://yj2025.com")
                .build();
        WxCpMessageSendResult yunji = tenantWxCpService.tenant("k8s-local").getMessageService().send(reply);
        System.out.println(yunji.toString());
    }


    /**
     * 手动初始化多租户配置，并测试发送
     *
     * @throws WxErrorException
     */
    @Test
    public void testManualInitConfig() throws WxErrorException {
        TenantWxCpConfigOperator configOperator = tenantWxCpService.getTenantOperator();
        configOperator.setConfigs(
                new TenantWxCpConfig()
                        .setTenantId("feike")
                        .setCorpId("wx7003aae3ac")
                        .setCorpSecret("f4Q3KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ")
                        .setToken("6HFXyimVN37E5f")
                        .setAesKey("oHhKlG1x37YXFkwg9Ncglm2wfIANxFAGn9")
                        .setAgentId(1000003)
        );
        this.sendDemoMessage("feike");
    }

    /**
     * 测试调用bean修改其中某个租户的配置，并发送
     *
     * @throws WxErrorException
     */
    @Test
    public void testUpdateConfig() throws WxErrorException {
        TenantWxCpConfigOperator tenantOperator = tenantWxCpService.getTenantOperator();
        tenantOperator.setCorpSecret("k8s-local", "f4QXoH0x5KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ");
        this.sendDemoMessage("k8s-local");
    }

}
