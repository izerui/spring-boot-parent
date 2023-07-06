package com.yj2025.jdbc.tenant;

import org.springframework.context.ApplicationContext;

public class TenantSharding extends AbstractTableSharding {

    public TenantSharding(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected String getTable(String sourceTable, String tenantId) {
        return sourceTable.concat("_").concat(tenantId);
    }
}
