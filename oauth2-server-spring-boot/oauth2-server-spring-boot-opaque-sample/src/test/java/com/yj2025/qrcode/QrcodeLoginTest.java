package com.yj2025.qrcode;

import com.yj2025.oauth2.opaque.OpaqueServerSampleApplication;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpaqueServerSampleApplication.class)
public class QrcodeLoginTest {

    @Autowired
    private QrcodeService qrcodeService;

    @Test
    public void scan() {
        String ticket = "https://api.yj2025.com/qrcode/redirect?q=ssoY3W5MX49Ih5E7HBgIUU9Ei5ssFAxXZ";
        qrcodeService.scanLogin(ticket,false,"acccc","eeee","https://p.qqan.com/up/2022-4/16512906167642988.jpg");
    }

    @Test
    public void login() {
        String ticket = "https://api.yj2025.com/qrcode/redirect?q=ssoY3W5MX49Ih5E7HBgIUU9Ei5ssFAxXZ";
        qrcodeService.scanLogin(ticket,true,"acccc","eeee","https://p.qqan.com/up/2022-4/16512906167642988.jpg");
    }
}
