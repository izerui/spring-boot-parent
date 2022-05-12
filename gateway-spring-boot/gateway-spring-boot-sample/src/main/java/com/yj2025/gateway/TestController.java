package com.yj2025.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(@RequestHeader(value = "accountName",required = false) String accountName) {
        System.out.println(accountName);
        return "success" + accountName;
    }
}
