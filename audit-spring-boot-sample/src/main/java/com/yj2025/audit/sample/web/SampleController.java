package com.yj2025.audit.sample.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
