package com.yj2025.sharding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class Sharding extends AbstractTableSharding {

    @Value("${sharding.underline-tablename:false}")
    private Boolean underlineTablename;

    public Sharding(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected String tableName(String sourceTable, String tenantId) {
        if (underlineTablename) {
            return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_"));
        } else {
            return sourceTable.concat("_").concat(tenantId);
        }
    }

    @Override
    protected String tableName(String sourceTable, String tenantId, String year) {
        if (underlineTablename) {
            return sourceTable.concat("_").concat(StringUtils.replace(tenantId, "-", "_")).concat("_").concat(year);
        } else {
            return sourceTable.concat("_").concat(tenantId).concat("_").concat(year);
        }
    }
}
