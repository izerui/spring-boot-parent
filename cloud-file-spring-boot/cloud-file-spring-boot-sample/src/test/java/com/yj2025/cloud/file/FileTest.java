package com.yj2025.cloud.file;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class FileTest {

    @Autowired
    private CloudFileProductManager cloudFileProductManager;

    @Test
    public void test() {
        CloudFileProperties.Bucket erp = cloudFileProductManager.getBucket("erp", true);
        log.info("测试");
    }
}
