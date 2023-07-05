package com.yj2025.jdbc.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public abstract class AbstractShardingRule {

    protected List<String> cacheTables;

    public AbstractShardingRule() {
    }

    public String getTableName(DataSource dataSource, String sourceTableName, String entCode, Object... params) {
        Assert.state(!StringUtils.isEmpty(sourceTableName), "AbstractRule: [tablePrefix]不能为空");
        String tableName = this.getTableName(sourceTableName, entCode, params);
        // 如果未缓存当前库的所有表，则获取并放入缓存
        if (cacheTables == null) {
            cacheTables = getTables(dataSource);
        }
        if (cacheTables.contains(tableName)) {
            return tableName;
        } else {
            log.debug("路由目的表: [{}] 在数据库中不存在, 故使用源表: [{}]", tableName, sourceTableName);
            return sourceTableName;
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

    protected abstract String getTableName(String tablePrefix, String entCode, Object... params);
}
