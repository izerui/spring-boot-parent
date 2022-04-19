package com.yj2025.weixin.work;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

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
    private TenantWxCpService tenantWxCpService;

    @Override
    public void run(String... args) throws Exception {
//        tenantWxCpService.getConfigOperator().setConfigs(
//                new TenantConfig()
//                        .setTenantId("yunji")
//                        .setCorpId("ww7c4f40dafaee2f4c")
//                        .setCorpSecret("c3fMZXD7qjfLz4ghNxhE8_SjsmYizPwf_QmcDISyXBM")
//                        .setToken("3ORc6qO5uMJeQNmbd9Tf1b27w")
//                        .setAesKey("WGSJDot0bvUy72RMeqoi5966lsFyuzdcQiSSntpUtu2")
//                        .setAgentId(1000013),
//                new TenantConfig()
//                        .setTenantId("jingguan")
//                        .setCorpId("ww7c4f40dafaee2f4c")
//                        .setCorpSecret("hgKyD3RjUD43v0E2N2C2Pzfd8BKDyG8AlP1EntU923I")
//                        .setAgentId(1000017)
//        );
        sendDemoMessage();
//        new CountDownLatch(1).await();
    }

    private void sendDemoMessage() throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");

        WxCpMessageSendResult messageSendResult = this.tenantWxCpService.tenant("jingguan").getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }
}
