package com.yj2025.dynamic;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.yj2025.tenant.TenantHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuyuhua
 */
public class TenantDynamicDataSource extends DynamicRoutingDataSource {

    private final DefaultDataSourceCreator defaultDataSourceCreator;
    private final TenantDatasourceProperties properties;


    /**
     * 所有配置了的租户数据库
     */
    private final Map<String, DataSource> tenantDataSourceMap = new ConcurrentHashMap<>();

    public TenantDynamicDataSource(DefaultDataSourceCreator defaultDataSourceCreator, TenantDatasourceProperties properties) {
        this.defaultDataSourceCreator = defaultDataSourceCreator;
        this.properties = properties;
    }


    @Override
    public DataSource determineDataSource() {
        DataSource dataSource = null;
        String tenantId = TenantHolder.getTenantId();
        if (tenantId != null && !"".equals(tenantId)) {
            dataSource = getTenantDatasource(tenantId);
        }
        return dataSource != null ? dataSource : super.determineDataSource();
    }

    private DataSource getTenantDatasource(String tenantId) {
        return tenantDataSourceMap.get(tenantId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.tenantDataSourceMap.putAll(loadTenantDatasource());
    }

    private Map<String, DataSource> loadTenantDatasource() {
        Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();
        Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
        for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
            String dsName = item.getKey();
            DataSourceProperty dataSourceProperty = item.getValue();
            String poolName = dataSourceProperty.getPoolName();
            if (poolName == null || "".equals(poolName)) {
                poolName = dsName;
            }
            dataSourceProperty.setPoolName(poolName);
            dataSourceMap.put(dsName, defaultDataSourceCreator.createDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }
}
