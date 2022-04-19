package com.yj2025.weixin.work;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@Data
@ConfigurationProperties(prefix = "work.weixin")
public class WorkWeixinProperties {

    private StorageType storage = StorageType.memory;
    /**
     * 代理服务器配置
     */
    private HttpProxy proxy = new HttpProxy();
    /**
     * # 最大重试次数，默认：5 次，如果小于 0，则为 0
     */
    private Integer maxRetryTimes = 5;
    /**
     * # 重试时间间隔步进，默认：1000 毫秒，如果小于 0，则为 1000
     */
    private Integer retrySleepMillis = 1000;

    private File tmpDirFile;

    @Data
    public static class HttpProxy {
        private String httpProxyHost;
        private Integer httpProxyPort = 0;
        private String httpProxyUsername;
        private String httpProxyPassword;
    }

    public enum StorageType {
        memory, redis;
    }
}
