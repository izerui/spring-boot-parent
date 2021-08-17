package com.yj2025.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JobDemoService {

    @XxlJob("test01")
    public void test01() throws InterruptedException {
        XxlJobHelper.log("XXL-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        // default success
    }

    @XxlJob("test02")
    public void test02() {
        throw new RuntimeException("不支持");
        // default success
    }
}
