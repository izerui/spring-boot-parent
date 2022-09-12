package com.yj2025.cloud.file.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.yj2025.cloud.file.CloudFileException;
import com.yj2025.cloud.file.CloudFileManager;
import com.yj2025.cloud.file.CloudFileProperties;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;

/**
 * @author liuyuhua
 */
public class CloudFileManagerImpl implements CloudFileManager {

    private final CloudFileProperties properties;

    private transient Auth auth;
    private transient Configuration config;
    private transient UploadManager uploadManager;
    private transient BucketManager bucketManager;

    public CloudFileManagerImpl(CloudFileProperties properties) throws Exception {
        this.properties = properties;
        init();
    }

    private void init() throws Exception {
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());

        this.config = new Configuration(properties.getRegion().getRegion());
        this.config.useHttpsDomains = properties.isUploadByHttps();
        this.config.putThreshold = properties.getPutThreshold();
        this.config.connectTimeout = properties.getConnectTimeout();
        this.config.writeTimeout = properties.getWriteTimeout();
        this.config.readTimeout = properties.getResponseTimeout();
        this.config.retryMax = properties.getMaxRetryTimes();

        this.uploadManager = new UploadManager(config, new FileRecorder(properties.getFileRecordDirectory()));
        this.bucketManager = new BucketManager(auth, config);
    }

    @Override
    public FileInfo getFileInfo(String bucket, String key) {
        try {
            FileInfo fileInfo = bucketManager.stat(bucket, key);
            return fileInfo;
        } catch (QiniuException e) {
            throw new CloudFileException(e.getMessage(), e);
        }
    }

    @Override
    public CloudFileProperties.Bucket getBucket(boolean isPublic) {
        if (isPublic) {
            return properties.getFirstPublicBucket();
        } else {
            return properties.getFirstPrivateBucket();
        }
    }

    @Override
    public CloudFileProperties.Bucket getBucket(String bucket) {
        return properties.getBucketByname(bucket);
    }

    @Override
    public String getUploadToken(String bucket, String key) {
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append("\"key\":\"$(key)\",")
                .append("\"etag\":\"$(etag)\",")
                .append("\"fileSize\":$(fsize),")
                .append("\"mimeType\":\"$(mimeType)\",")
                .append("\"ext\":\"$(ext)\",")
                .append("\"bucket\":\"$(bucket)\"")
                .append("}");
        final String returnBody = builder.toString();
        StringMap policy = new StringMap();
        //policy.put("insertOnly", 1);
        policy.put("returnBody", returnBody);
        return auth.uploadToken(bucket, key, 3600, policy);
    }

    @Override
    public void upload(String bucket, String key, byte[] bytes) {
        try {
            String token = getUploadToken(bucket, key);
            this.uploadManager.put(bytes, key, token);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public void upload(String bucket, String key, File file) {
        try {
            String token = getUploadToken(bucket, key);
            this.uploadManager.put(file, key, token);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public void upload(String bucket, String key, String filePath) {
        try {
            String token = getUploadToken(bucket, key);
            this.uploadManager.put(filePath, key, token);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public void upload(String bucket, String key, InputStream inputStream, String mime) {
        try {
            String token = getUploadToken(bucket, key);
            this.uploadManager.put(inputStream, key, token, null, mime);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getDownloadUrl(String bucket, String key, String attName) {
        return getDownloadUrl(bucket, key, attName, null);
    }

    @Override
    public String getDownloadUrl(String bucket, String key, String attName, String fop) {
        try {
            CloudFileProperties.Bucket cloudBucket = properties.getBucketByname(bucket);
            Assert.notNull(cloudBucket, "未找到名称为" + cloudBucket + "的存储空间配置");
            DownloadUrl downloadUrl = new DownloadUrl(cloudBucket.getDomain(), cloudBucket.getUseHttps(), key);
            downloadUrl.setAttname(attName);
            downloadUrl.setFop(fop);
            if (cloudBucket.getIsPublic()) {
                return downloadUrl.buildURL();
            } else {
                return downloadUrl.buildURL(auth, System.currentTimeMillis() / 1000 + properties.getDownloadExpiresSeconds());
            }
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getPreviewUrl(String bucket, String key, Integer width, Integer height) {
        return getDownloadUrl(bucket, key, null, "imageView2/2/w/" + width + "/h/" + height);
    }

    @Override
    public String getPreviewUrl(String bucket, String key) {
        return getDownloadUrl(bucket, key, null);
    }
}
