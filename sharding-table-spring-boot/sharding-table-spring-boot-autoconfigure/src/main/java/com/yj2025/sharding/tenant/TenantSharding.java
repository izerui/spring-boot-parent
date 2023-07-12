package com.yj2025.sharding.tenant;

import org.springframework.context.ApplicationContext;

public class TenantSharding extends AbstractTableSharding {

    public TenantSharding(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId) {
        return sourceTable.concat("_").concat(tenantId);
    }
}
