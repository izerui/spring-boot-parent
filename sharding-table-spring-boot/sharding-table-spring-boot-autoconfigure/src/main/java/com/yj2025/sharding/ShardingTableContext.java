package com.yj2025.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class ShardingTableContext {

    @Autowired
    private ShardingProperties shardingProperties;


    /**
     * 根据表名和账套编号及其他参数进行路由新的表名
     *
     * @param tablePrefix 源表名
     * @param entCode     账套编号
     * @param params      其他参数
     * @return 新的表名
     */
    public String getTableName(DataSource dataSource, String tablePrefix, String entCode, String... params) {
        Map<String, ShardingTableProperties> tables = shardingProperties.getTables();
        if (tables != null) {
            ShardingTableProperties tableProperties = tables.get(tablePrefix);
            if (tableProperties != null) {
                AbstractRule rule = tableProperties.getRule();
                Assert.state(rule != null, "ShardingTables: " + tablePrefix + " 未配置有效的路由规则");
                return rule.getTableName(tableProperties, dataSource, tablePrefix, entCode, params);
            }
        }
        throw new RuntimeException("[" + tablePrefix + "]未找到分表路由配置");
    }
}
