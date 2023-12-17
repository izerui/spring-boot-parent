package com.yj2025.sharding;

import com.yj2025.tenant.TenantHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public abstract class AbstractTableSharding {

    protected ApplicationContext applicationContext;
    protected final ShardingProperties properties;

    protected final static Map<DataSource, List<String>> cacheDataSourceTablesMap = new ConcurrentHashMap<>();

    public AbstractTableSharding(ApplicationContext applicationContext, ShardingProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    /**
     * 获取自动租户id的表名
     *
     * @param sourceTable
     * @return
     */
    public final String getTable(String sourceTable) {
        Assert.state(!StringUtils.isEmpty(sourceTable), "AbstractRule: [tablePrefix]不能为空");
        String tenantId = TenantHolder.getTenantId();
        String year = TenantHolder.getYear();
        return this.getTable(sourceTable, tenantId, year);
    }

    /**
     * 通过租户id获取租户表名
     *
     * @param sourceTable
     * @param tenantId
     * @return
     */
    public final String getTable(String sourceTable, String tenantId) {
        return this.getTable(applicationContext.getBean(DataSource.class), sourceTable, tenantId, null);
    }


    /**
     * 通过租户id获取租户表名
     *
     * @param sourceTable
     * @param tenantId
     * @return
     */
    public final String getTable(String sourceTable, String tenantId, String year) {
        return this.getTable(applicationContext.getBean(DataSource.class), sourceTable, tenantId, year);
    }

    /**
     * 获取指定数据源、指定租户id、指定年度的表名
     *
     * @param dataSource
     * @param sourceTable
     * @param tenantId
     * @return
     */
    public final String getTable(DataSource dataSource, String sourceTable, String tenantId) {
        return this.getTable(dataSource, sourceTable, tenantId, null);
    }


    /**
     * 获取指定数据源、指定租户id、指定年度的表名
     *
     * @param dataSource
     * @param sourceTable
     * @param tenantId
     * @param year
     * @return
     */
    public final String getTable(DataSource dataSource, String sourceTable, String tenantId, String year) {
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("使用sharding获取分表结果,但是入口方法未正确声明@Tenant注解, 或者无法获取有效的tenant信息");
        }
        String tenantTable = this.tableName(sourceTable, tenantId);
        String tenantYearTable = null;
        if (year != null) {
            tenantYearTable = this.tableName(sourceTable, tenantId, year);
        }
        String table = switchTable(dataSource, sourceTable, tenantYearTable, tenantTable);
        return table;
    }

    @SneakyThrows
    private String switchTable(DataSource dataSource, String sourceTable, String... targetTables) {
        if (dataSource.getClass().getName().equals("com.baomidou.dynamic.datasource.DynamicRoutingDataSource")) {
            Method determineMethod = ReflectionUtils.findMethod(Class.forName("com.baomidou.dynamic.datasource.DynamicRoutingDataSource"), "determineDataSource");
            dataSource = (DataSource) ReflectionUtils.invokeMethod(determineMethod, dataSource);
        }
        // 如果未缓存当前库的所有表，则获取并放入缓存
        if (!cacheDataSourceTablesMap.containsKey(dataSource)) {
            cacheDataSourceTablesMap.put(dataSource, getTables(dataSource));
            log.info("缓存的数据源个数: {}", cacheDataSourceTablesMap.size());
        }
        List<String> cacheTables = cacheDataSourceTablesMap.get(dataSource);

        // 按顺序先找年表、再找租户表
        for (String targetTable : targetTables) {
            if (targetTable == null) {
                continue;
            }
            if (cacheTables != null && cacheTables.contains(targetTable)) {
                return targetTable;
            }
        }
        // 找到匹配到sourceTable的多个分拆后的表数量
        Function<String, String> targetNotFoundWarnFun = useTable -> {
            if (properties.getWarnForNotfound()) {
                long count = cacheTables.stream().filter(s -> s.startsWith(sourceTable)).count();
                log.warn("拆表数量:[{}] 路由目的表: [{}] 在数据库中不存在, 故使用源表: [{}]", count - 1, targetTables, sourceTable);
            }
            return useTable;
        };

        // 如果年表和租户表都找不到则按有限匹配到的顺序返回 【源表_runtime】 和 【源表】
        if (cacheTables != null && cacheTables.contains(sourceTable + "_runtime")) {
            return targetNotFoundWarnFun.apply(sourceTable + "_runtime");
        } else {
            return targetNotFoundWarnFun.apply(sourceTable);
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
        List<String> tables = jdbcTemplate.queryForList("/* 缓存当前" + dataSource + "所有表,用来分表路由 */ show tables", String.class);
        Collections.sort(tables);
        log.info("检测到数据库表:");
        log.info("------------------------------------------------");
        tables.forEach(s -> log.info("{}", s));
        log.info("------------------------------------------------");
        return tables;
    }

    /**
     * 不开放给public调用，因为必须经过缓存列表验证一道
     *
     * @param sourceTable
     * @param tenantId
     * @return
     */
    protected abstract String tableName(String sourceTable, String tenantId);

    /**
     * 不开放给public调用，因为必须经过缓存列表验证一道
     *
     * @param sourceTable
     * @param tenantId
     * @param year
     * @return
     */
    protected abstract String tableName(String sourceTable, String tenantId, String year);
}
