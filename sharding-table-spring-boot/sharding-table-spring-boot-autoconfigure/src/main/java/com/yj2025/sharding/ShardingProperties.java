package com.yj2025.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sharding")
public class ShardingProperties {

    private Boolean underlineTablename = false;

    private Boolean warnForNotfound = true;

}
