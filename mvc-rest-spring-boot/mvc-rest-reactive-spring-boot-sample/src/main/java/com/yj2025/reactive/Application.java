package com.yj2025.reactive;

import com.ecworking.system.vo.RecAgreementVo;
import com.yj2025.reactive.remote.ReceiveAgreementClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yj2025.reactive.remote")
public class Application implements CommandLineRunner {

    @Autowired
    private ReceiveAgreementClient agreementClient;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<RecAgreementVo> enableRecAgreements = agreementClient.findEnableRecAgreements("333367878");
        log.info("{}", enableRecAgreements);
    }
}
