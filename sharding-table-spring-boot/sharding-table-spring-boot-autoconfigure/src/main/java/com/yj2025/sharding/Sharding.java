package com.yj2025.sharding;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class Sharding extends AbstractTableSharding {


    public Sharding(ApplicationContext applicationContext, ShardingProperties properties) {
        super(applicationContext, properties);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId) {
        if (properties.getUnderlineTablename()) {
            return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_"));
        } else {
            return sourceTable.concat("_").concat(tenantId);
        }
    }

    @Override
    protected String tableName(String sourceTable, String tenantId, String year) {
        if (properties.getUnderlineTablename()) {
            return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_")).concat("_").concat(year);
        } else {
            return sourceTable.concat("_").concat(tenantId).concat("_").concat(year);
        }
    }
}
