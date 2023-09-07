package com.yj2025.sample;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "测试tenantId")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Operation(summary = "测试tenant")
    @GetMapping("/testTenant")
    public String testTenant() {
        return tenantService.testTenant2();
    }
}
