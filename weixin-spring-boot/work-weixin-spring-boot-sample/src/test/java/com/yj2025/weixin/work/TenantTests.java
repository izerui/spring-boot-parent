package com.yj2025.weixin.work;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
     * @throws WxErrorException
     */
    @Test
    public void testSpring() throws WxErrorException {
        this.sendDemoMessage("feike");
    }


    /**
     * 手动初始化多租户配置，并测试发送
     * @throws WxErrorException
     */
    @Test
    public void testManualInitConfig() throws WxErrorException {
        TenantConfigOperator configOperator = tenantWxCpService.getConfigOperator();
        configOperator.setConfigs(
                new TenantConfig()
                        .setTenantId("feike")
                        .setCorpId("wx7004ac2607aae3ac")
                        .setCorpSecret("f4QXoH0x5KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ")
                        .setToken("6HFXyimVNitD3REk87E5f")
                        .setAesKey("oHhKlG1xj2aEyZM2WC7YXFkwg9Ncglm2wfIANxFAGn9")
                        .setAgentId(1000003)
        );
        this.sendDemoMessage("feike");
    }

    /**
     * 测试调用bean修改其中某个租户的配置，并发送
     * @throws WxErrorException
     */
    @Test
    public void testUpdateConfig() throws WxErrorException {
        TenantConfigOperator configOperator = tenantWxCpService.getConfigOperator();
        configOperator.setCorpSecret("feike", "f4QXoH0x5KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ");
        this.sendDemoMessage("feike");
    }

    /**
     * 通过消息回调里面的 agentId 获取对应的配置
     */
    @Test
    public void testGetConfig() {
        String agentId = "1000003";
        TenantConfigOperator configOperator = tenantWxCpService.getConfigOperator();
        String tenantId = configOperator.getTenantIdByAgentId(agentId);
        TenantConfig config = configOperator.getConfig(tenantId);
        System.out.println(config.toString());
    }

}
