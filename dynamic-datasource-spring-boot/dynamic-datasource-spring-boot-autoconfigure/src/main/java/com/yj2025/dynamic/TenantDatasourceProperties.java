package com.yj2025.dynamic;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = TenantDatasourceProperties.PREFIX)
public class TenantDatasourceProperties {
    public static final String PREFIX = "spring.datasource.tenant";

    /**
     * 定义的每个租户的数据源
     */
    private Map<String, DataSourceProperty> datasource = new LinkedHashMap<>();
}
