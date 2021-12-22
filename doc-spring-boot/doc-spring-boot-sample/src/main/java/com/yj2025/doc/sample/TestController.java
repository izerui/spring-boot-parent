package com.yj2025.doc.sample;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @ApiOperation("测试doc")
    @GetMapping("/test")
    public String test(@RequestParam(value = "name",defaultValue = "123") String name) {
        return "hello " + name + "!!!";
    }
}
