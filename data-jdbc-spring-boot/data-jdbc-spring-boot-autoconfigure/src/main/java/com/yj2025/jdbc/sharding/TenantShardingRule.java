package com.yj2025.jdbc.sharding;

public class TenantShardingRule extends AbstractShardingRule {

    @Override
    protected String getTableName(String sourceTableName, String entCode, Object... params) {
        return sourceTableName.concat("_").concat(entCode);
    }
}
