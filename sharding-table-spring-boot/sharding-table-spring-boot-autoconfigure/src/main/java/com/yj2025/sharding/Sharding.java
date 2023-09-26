package com.yj2025.sharding;

import org.springframework.context.ApplicationContext;

public class Sharding extends AbstractTableSharding {

    public Sharding(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId) {
        return sourceTable.concat("_").concat(tenantId);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId, String year) {
        return sourceTable.concat("_").concat(tenantId).concat("_").concat(year);
    }
}
