package com.yj2025.sharding;

public class TenantRule extends AbstractRule {

    @Override
    protected String getTableName(String tablePrefix, String entCode, Object... params) {
        return tablePrefix.concat("_").concat(entCode);
    }
}
