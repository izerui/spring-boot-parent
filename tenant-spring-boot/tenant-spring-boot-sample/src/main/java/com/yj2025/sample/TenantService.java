package com.yj2025.sample;

import com.yj2025.tenant.Tenant;
import com.yj2025.tenant.TenantHolder;
import org.springframework.stereotype.Service;

@Service
public class TenantService {

    @Tenant("#{#tenantId}")
    public void testTenant(String tenantId) {
        System.out.println("tenantId: " + tenantId);
        System.out.println("tenantHolder: " + TenantHolder.getTenantId());
    }
}
