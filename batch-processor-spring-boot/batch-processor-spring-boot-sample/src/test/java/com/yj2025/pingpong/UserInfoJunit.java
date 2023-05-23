package com.yj2025.pingpong;

import com.yj2025.commons.vo.RespVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class UserInfoJunit {


    @Test
    public void testRbacRestart() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        while (true) {
            try {
                ResponseEntity<RespVO> response = restTemplate.getForEntity("https://api-uat.yj2025.com/system-setting-pc/v1/user/info?access_token=6894398c-f5e0-49d8-9de6-89f027822f66", RespVO.class);
                RespVO resp = response.getBody();
                if (resp != null && resp.isSuccess()) {
                    log.info("{}: {}", DateTime.now().toString("HH:mm:ss"), resp.isSuccess());
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                log.error("{}: {}", DateTime.now().toString("HH:mm:ss"), ex.getMessage());
            }
        }
    }
}
