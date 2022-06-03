package com.yj2025.weixin.work22;

import me.chanjar.weixin.common.error.WxErrorException;
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
     * @throws WxErrorException
     */
    @Test
    public void test01() throws WxErrorException {
        // 获取第三方应用凭证: https://developer.work.weixin.qq.com/document/path/90600
        String suiteAccessToken = wxCpTpService.getSuiteAccessToken();
        System.out.println("第三方应用凭证: " + suiteAccessToken);

        // 获取预授权链接(正式环境更换同名接口): https://developer.work.weixin.qq.com/document/path/90601
        String preAuthUrl = wxCpTpService.getPreAuthUrl("https://local-dev.yj2025.com", "abc", 1);
        System.out.println("安装第三方应用地址: "+ preAuthUrl);


    }

}
