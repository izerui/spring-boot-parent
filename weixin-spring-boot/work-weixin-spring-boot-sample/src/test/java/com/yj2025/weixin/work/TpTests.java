package com.yj2025.weixin.work;

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

    @Test
    public void test01() {
    }
}
