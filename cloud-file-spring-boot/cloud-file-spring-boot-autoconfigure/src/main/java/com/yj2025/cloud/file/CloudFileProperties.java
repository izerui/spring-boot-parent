package com.yj2025.cloud.file;

import com.qiniu.common.Constants;
import com.qiniu.storage.Region;
import com.yj2025.commons.exception.BusinessException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Comparator;
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
     * 是否开启动态bucket， 开启后会根据不同的entCode，动态生成Bucket对象，而不再是抛出new BusinessException("文件的bucket为空,无法获取文件URL")异常
     */
    private boolean enableDynamicBucket = false;

    /**
     * 动态bucket的桶前缀
     */
    private String dynamicBucketPrefix = "file-p3-";

    /**
     * 动态bucket的域名后缀, 注意必须以点开头,会自动拼接桶前缀组成自定义文件桶的域名 例如： .yj2025.com
     */
    private String dynamicBucketDomainSuffix = ".yj2025.com";

    /**
     * 动态bucket的域名是否使用https
     */
    private boolean dynamicBucketUseHttps = false;

    /**
     * 支持上传的桶
     */
    @NestedConfigurationProperty
    private Map<String, Bucket> buckets = new HashMap<>();


    /**
     * 获取第一个public桶
     *
     * @return
     */
    public Bucket getFirstPublicBucket() {
        return buckets.values().stream().filter(bucket -> bucket.isPublic).sorted(Comparator.comparing(o -> o.isDefault ? 0 : 1)).findFirst().orElseThrow();
    }

    /**
     * 获取第一个private桶
     *
     * @return
     */
    public Bucket getFirstPrivateBucket() {
        return buckets.values().stream().filter(bucket -> !bucket.isPublic).sorted(Comparator.comparing(o -> o.isDefault ? 0 : 1)).findFirst().orElseThrow();
    }

    /**
     * 根据名字获取桶配置
     *
     * @param bucket
     * @return
     */
    public Bucket getBucketByname(String bucket) {
        return buckets.values().stream()
                .filter(b -> b.getBucketName().equals(bucket)).findFirst()
                .orElseGet(() -> {
                    if (enableDynamicBucket) {
                        Bucket bkt = new Bucket();
                        bkt.setBucketName(dynamicBucketPrefix.concat(bucket));
                        bkt.setDomain(dynamicBucketPrefix.concat(bucket).concat(dynamicBucketDomainSuffix));
                        bkt.setIsDefault(false);
                        // 动态桶默认只支持私有桶
                        bkt.setIsPublic(false);
                        bkt.setUseHttps(dynamicBucketUseHttps);
                        return bkt;
                    } else {
                        throw new BusinessException("文件的bucket为空,无法获取文件URL");
                    }
                });
    }

    @Data
    public static class Bucket {
        private Boolean isPublic = true;
        private Boolean isDefault;
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
