package com.yj2025.dynamic;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;

import javax.sql.DataSource;
import java.util.Map;

public class TenantDatasourceProvider implements DynamicDataSourceProvider {
    @Override
    public Map<String, DataSource> loadDataSources() {
        return null;
    }
}
