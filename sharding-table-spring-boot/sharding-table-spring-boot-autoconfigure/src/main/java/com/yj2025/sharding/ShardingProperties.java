package com.yj2025.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "sharding")
public class ShardingProperties {

    @NestedConfigurationProperty
    private Map<String, ShardingTableProperties> tables;
}
