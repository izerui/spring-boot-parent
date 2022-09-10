package com.yj2025.cloud.file;

import com.qiniu.common.Constants;
import com.qiniu.storage.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "file.storage")
public class CloudFileProperties {
    /**
     * appkey
     */
    private String accessKey;
    /**
     * app密钥
     */
    private String secretKey;
    /**
     * 存储区域(区域名称：z0 华东  z1 华北  z2 华南  na0 北美  as0 东南亚)
     **/
    private RegionEnum region = RegionEnum.z2;

    /**
     * 上传是否使用 https, 默认否
     **/
    private boolean uploadByHttps = false;

    /**
     * 断点续传信息存放目录, 为了实现断点续传功能, 必须在本机某个目录下记录断点续传信息
     **/
    private String fileRecordDirectory;

    /**
     * 如果文件大小大于此值则使用断点上传, 否则使用Form上传, 单位字节
     **/
    private int putThreshold = Constants.BLOCK_SIZE;

    /**
     * 连接超时时间, 单位秒(默认10s)
     **/
    private int connectTimeout = Constants.CONNECT_TIMEOUT;

    /**
     * 写超时时间, 单位秒(默认 0, 不超时)
     **/
    private int writeTimeout = Constants.WRITE_TIMEOUT;

    /**
     * 回复超时时间, 单位秒(默认30s)
     **/
    private int responseTimeout = Constants.READ_TIMEOUT;

    /**
     * 上传失败重试次数
     **/
    private int maxRetryTimes = 5;

    /**
     * 私有空间下载url有效时长5分钟（单位:秒）
     */
    private long downloadExpiresSeconds = 300;

    /**
     * 支持上传的桶
     */
    @NestedConfigurationProperty
    private Map<String, Bucket> buckets = new HashMap<>();

    @Data
    public static class Bucket {
        private Boolean isPublic = true;
        private String domain;
        private Boolean useHttps = true;
        private String bucketName;
    }

    /**
     * z0 华东  z1 华北  z2 华南  na0 北美  as0 东南亚
     */
    public enum RegionEnum {
        z0("华东", Region.region0()),
        z1("华北", Region.region1()),
        z2("华南", Region.region2()),
        na0("北美", Region.regionNa0()),
        as0("东南亚", Region.regionAs0());

        private String regionName;
        private Region region;

        RegionEnum(String regionName, Region region) {
            this.regionName = regionName;
            this.region = region;
        }

        public String getRegionName() {
            return regionName;
        }

        public Region getRegion() {
            return region;
        }
    }
}
