package com.yj2025.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sharding")
public class ShardingProperties {

    /**
     * 是否统一转换中划线为下划线的表名
     */
    private Boolean underlineTablename = false;

    /**
     * 如果未找到目的表是否输出警告
     */
    private Boolean warnForNotfound = true;

    /**
     * 是否输出找到的目的表
     */
    private Boolean infoForFound = false;

    /**
     * 如果使用租户id+年度找到的路由表跟首次缓存记录的路由表不一致则报错
     */
    private Boolean exceptionForDifference = true;

}
