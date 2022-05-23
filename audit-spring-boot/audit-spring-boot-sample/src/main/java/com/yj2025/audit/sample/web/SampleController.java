package com.yj2025.audit.sample.web;

import com.yj2025.audit.sample.listener.AuditListener;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private AuditListener auditListener;

    @GetMapping("/clear")
    public String clear() {
        auditListener.clear();
        return "clear";
    }

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
