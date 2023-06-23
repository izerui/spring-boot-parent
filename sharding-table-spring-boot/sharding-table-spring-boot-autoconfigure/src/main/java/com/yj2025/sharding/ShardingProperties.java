package com.yj2025.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "sharding")
public class ShardingProperties {

    private Map<String, ShardingTableProperties> tables;
}
