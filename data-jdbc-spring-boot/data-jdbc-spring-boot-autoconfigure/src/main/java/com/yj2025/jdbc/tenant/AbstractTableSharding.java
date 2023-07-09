package com.yj2025.jdbc.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractTableSharding {

    private ApplicationContext applicationContext;

    protected final static Map<DataSource, List<String>> cacheDataSourceTablesMap = new ConcurrentHashMap<>();

    public AbstractTableSharding(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public final String getTable(String sourceTable) throws Exception {
        Assert.state(!StringUtils.isEmpty(sourceTable), "AbstractRule: [tablePrefix]不能为空");
        String tenantId = TenantThreadLocalHolder.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("因为@Table使用了sharding分表表达式,但是入口方法未正确声明@TenantThreadLocal(\"#{#entCode}\")注解, 或者无法获取有效的tenantId");
        }
        String tableName = this.getTable(sourceTable, tenantId);
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        if (dataSource.getClass().getName().equals("com.baomidou.dynamic.datasource.DynamicRoutingDataSource")) {
            Method determineMethod = ReflectionUtils.findMethod(Class.forName("com.baomidou.dynamic.datasource.DynamicRoutingDataSource"), "determineDataSource");
            dataSource = (DataSource) ReflectionUtils.invokeMethod(determineMethod, dataSource);
        }
        // 如果未缓存当前库的所有表，则获取并放入缓存
        if (!cacheDataSourceTablesMap.containsKey(dataSource)) {
            cacheDataSourceTablesMap.put(dataSource, getTables(dataSource));
        }
        List<String> cacheTables = cacheDataSourceTablesMap.get(dataSource);
        if (cacheTables != null && cacheTables.contains(tableName)) {
            return tableName;
        } else {
            log.debug("路由目的表: [{}] 在数据库中不存在, 故使用源表: [{}]", tableName, sourceTable);
            return sourceTable;
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
        List<String> tables = jdbcTemplate.queryForList("/* 缓存当前datasource所有表,用来分表路由 */ show tables", String.class);
        return tables;
    }

    protected abstract String getTable(String sourceTable, String tenantId);
}
