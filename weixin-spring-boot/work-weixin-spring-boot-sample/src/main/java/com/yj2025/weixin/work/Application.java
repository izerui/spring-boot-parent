package com.yj2025.weixin.work;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@SpringBootApplication
public class Application implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    private TenantConfigOperator tenantConfigOperator;

    @Autowired
    private TenantWxCpService workWeixinService;

    @Override
    public void run(String... args) throws Exception {
        tenantConfigOperator.setConfigs(
                TenantConfig.builder()
                        .tenantId("yunji")
                        .corpId("ww7c4f40dafaee2f4c")
                        .corpSecret("c3fMZXD7qjfLz4ghNxhE8_SjsmYizPwf_QmcDISyXBM")
                        .token("3ORc6qO5uMJeQNmbd9Tf1b27w")
                        .aesKey("WGSJDot0bvUy72RMeqoi5966lsFyuzdcQiSSntpUtu2")
                        .agentId(1000013)
                        .build(),
                TenantConfig.builder()
                        .tenantId("jingguan")
                        .corpId("ww7c4f40dafaee2f4c")
                        .corpSecret("hgKyD3RjUD43v0E2N2C2Pzfd8BKDyG8AlP1EntU923I")
                        .agentId(1000017)
                        .build()
        );
        sendDemoMessage();
    }

    private void sendDemoMessage() throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");

        WxCpMessageSendResult messageSendResult = this.workWeixinService.tenant("jingguan").getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }
}
