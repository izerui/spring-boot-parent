package com.yj2025.sharding;

public class TenantRule extends AbstractRule {

    @Override
    protected String getTableName(String tablePrefix, String entCode, String... params) {
        return tablePrefix.concat("_").concat(entCode);
    }
}
