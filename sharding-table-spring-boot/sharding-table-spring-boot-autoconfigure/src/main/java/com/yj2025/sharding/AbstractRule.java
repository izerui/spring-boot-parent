package com.yj2025.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public abstract class AbstractRule {

    protected List<String> cacheTables;

    public AbstractRule() {
    }

    public String getTableName(ShardingTableProperties properties, DataSource dataSource, String tablePrefix, String entCode, String... params) {
        Assert.state(!StringUtils.isEmpty(tablePrefix), "AbstractRule: [tablePrefix]不能为空");
        String tableName = this.getTableName(tablePrefix, entCode, params);
        // 如果未缓存当前库的所有表，则获取并放入缓存
        if (cacheTables == null) {
            cacheTables = getTables(dataSource);
        }
        if (cacheTables.contains(tableName)) {
            return tableName;
        } else {
            log.warn("路由目的表: [{}] 在数据库中不存在, 故使用指定表: [{}]", tableName, properties.getOtherwise());
            return properties.getOtherwise();
        }
    }

    /**
     * 从数据源获取当前所有的表
     *
     * @param dataSource
     * @return
     */
    private List<String> getTables(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> tables = jdbcTemplate.queryForList("show tables", String.class);
        return tables;
    }

    protected abstract String getTableName(String tablePrefix, String entCode, String... params);
}
