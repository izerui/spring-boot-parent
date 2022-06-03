package com.yj2025.weixin.work22;

import com.yj2025.weixin.work.CpService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpTpAuthInfo;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TpTests {

    @Autowired
    private WxCpTpService wxCpTpService;

    /**
     * https://developer.work.weixin.qq.com/document/path/91201
     *
     * @throws WxErrorException
     */
    @Test
    public void test01() throws WxErrorException {
        // 获取第三方应用凭证: https://developer.work.weixin.qq.com/document/path/90600
        String suiteAccessToken = wxCpTpService.getSuiteAccessToken();
        System.out.println("第三方应用凭证: " + suiteAccessToken);

        // 获取预授权链接(正式环境更换同名接口): https://developer.work.weixin.qq.com/document/path/90601
        String preAuthUrl = wxCpTpService.getPreAuthUrl("https://local-dev.yj2025.com", "abc", 1);
        System.out.println("安装第三方应用地址: " + preAuthUrl);

        String authCorpId = "ww7c4f40dafaee2f4c";
        String permanentCode = "Xl06AJHZR5Vndf2GI7z8aWQ3sdxScop5cZAbuPbVTLs";
        WxCpTpAuthInfo info = wxCpTpService.getAuthInfo(authCorpId, permanentCode);
        System.out.println("企业信息: " + info.toJson());

        WxAccessToken corpToken = wxCpTpService.getCorpToken(authCorpId, permanentCode);
        System.out.println("企业token: " + corpToken.getAccessToken());
    }

    @Autowired
    private CpService cpService;


    @Test
    public void testSendMsg() throws WxErrorException {
        sendDemoMessage("yunji-wode");
    }

    private void sendDemoMessage(String tenantId) throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = cpService.tenant(tenantId, true).getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }


}
