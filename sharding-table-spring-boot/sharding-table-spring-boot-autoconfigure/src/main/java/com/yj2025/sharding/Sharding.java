package com.yj2025.sharding;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class Sharding extends AbstractTableSharding {

    public Sharding(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId) {
        return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_"));
    }

    @Override
    protected String tableName(String sourceTable, String tenantId, String year) {
        return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_")).concat("_").concat(year);
    }
}
