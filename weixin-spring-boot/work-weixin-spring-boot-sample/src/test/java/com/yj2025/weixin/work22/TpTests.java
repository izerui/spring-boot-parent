package com.yj2025.weixin.work22;

import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpUserService;
import me.chanjar.weixin.cp.bean.*;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.tp.service.WxCpTpContactService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TpTests {

    @Autowired
    private TpService tpService;

    /**
     * https://developer.work.weixin.qq.com/document/path/91201
     *
     * @throws WxErrorException
     */
    @Test
    @Deprecated
    public void test01() throws WxErrorException {
        // 获取第三方应用凭证: https://developer.work.weixin.qq.com/document/path/90600
        String suiteAccessToken = tpService.getSuiteAccessToken();
        System.out.println("第三方应用凭证: " + suiteAccessToken);

        // 获取预授权链接(正式环境更换同名接口): https://developer.work.weixin.qq.com/document/path/90601
        String preAuthUrl = tpService.getPreAuthUrl("https://local-dev.yj2025.com", "abc", 1);
        System.out.println("安装第三方应用地址: " + preAuthUrl);

        String authCorpId = "ww7c4f40dafaee2f4c";
        String permanentCode = "k6QRaIefAYf3Y_gxy5c1S-83vw8xFi-ZoXgV9MjtuxQ";
        WxCpTpAuthInfo info = tpService.getAuthInfo(authCorpId, permanentCode);
        System.out.println("企业信息: " + info.toJson());

        WxAccessToken corpToken = tpService.getCorpToken(authCorpId, permanentCode, true);
        System.out.println("企业token: " + corpToken.getAccessToken());


        // 激活码
        String activeCode = "LA100000001000000629AF7D6";
        String activeAccount = tpService.activeAccount(activeCode, authCorpId, "serv");
        System.out.println(activeAccount);


        String userId = tpService.getCpService("yunji-wode").getUserService().getUserId("13911523134");
        System.out.println("查询到手机号对应的userId: " + userId);

        WxCpTpContactSearch search = new WxCpTpContactSearch();
        search.setAuthCorpId(authCorpId)
                        .setAgentId(1000061)
                                .setLimit(50)
                                        .setType(1)
                                                .setQueryWord("s");
        WxCpTpContactSearchResp searchResp = tpService.getWxCpTpContactService().contactSearch(search);
        System.out.println(searchResp.toString());


        List<WxCpDepart> departList = tpService.getCpService("yunji-wode").getDepartmentService().list(null);
        System.out.println("查询到部门列表: " + departList);

    }

    @Autowired
    private CpService cpService;


    @Test
    public void testSendMsg() throws WxErrorException {
        WxCpUserService userService = cpService.tenant("yunji-wode", true).getUserService();
        String userId = userService.getUserId("13911523134");

        WxCpUser byId = userService.getById(userId);
        System.out.println(byId.toJson());


        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser(userId);
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = cpService.tenant("yunji-wode", true).getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }


}
