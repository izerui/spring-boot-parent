package com.yj2025.cloud.file.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
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
import com.yj2025.cloud.file.UploadResponse;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;

/**
 * @author liuyuhua
 */
public class CloudFileManagerImpl implements CloudFileManager {

    private final CloudFileProperties properties;

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private transient Auth auth;
    private transient UploadManager uploadManager;
    private transient BucketManager bucketManager;


    public CloudFileManagerImpl(CloudFileProperties properties) throws Exception {
        this.properties = properties;
        init();
    }

    private void init() throws Exception {
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());

        Configuration config = new Configuration(properties.getRegion().getRegion());
        config.useHttpsDomains = properties.isUploadByHttps();
        config.putThreshold = properties.getPutThreshold();
        config.connectTimeout = properties.getConnectTimeout();
        config.writeTimeout = properties.getWriteTimeout();
        config.readTimeout = properties.getResponseTimeout();
        config.retryMax = properties.getMaxRetryTimes();

        this.uploadManager = new UploadManager(config, new FileRecorder(properties.getFileRecordDirectory()));
        this.bucketManager = new BucketManager(auth, config);
    }

    @Override
    public FileInfo getFileInfo(String bucket, String fileId) {
        try {
            return bucketManager.stat(bucket, fileId);
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
    public String getUploadToken(String bucket, String fileId) {
        // https://developer.qiniu.com/kodo/1235/vars#magicvar
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append("\"bucket\":\"$(bucket)\",")
                .append("\"fileId\":\"$(key)\",")
                .append("\"hash\":\"$(etag)\",")
                .append("\"fileSize\":$(fsize),")
                .append("\"fileName\":$(fname),")
                .append("\"filePrefix\":$(fprefix),")
                .append("\"mimeType\":\"$(mimeType)\",")
                .append("\"ext\":\"$(ext)\",")
                .append("\"imageInfo\":\"$(imageInfo)\"")
                .append("}");
        final String returnBody = builder.toString();
        StringMap policy = new StringMap();
        policy.put("returnBody", returnBody);
        return auth.uploadToken(bucket, fileId, 3600, policy);
    }

    @Override
    public UploadResponse upload(String bucket, String fileId, byte[] bytes) {
        try {
            String token = getUploadToken(bucket, fileId);
            Response response = this.uploadManager.put(bytes, fileId, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(String bucket, String fileId, File file) {
        try {
            String token = getUploadToken(bucket, fileId);
            Response response = this.uploadManager.put(file, fileId, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(String bucket, String fileId, String filePath) {
        try {
            String token = getUploadToken(bucket, fileId);
            Response response = this.uploadManager.put(filePath, fileId, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(String bucket, String fileId, InputStream inputStream, String mime) {
        try {
            String token = getUploadToken(bucket, fileId);
            Response response = this.uploadManager.put(inputStream, fileId, token, null, mime);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getDownloadUrl(String bucket, String fileId, String attName) {
        return getDownloadUrl(bucket, fileId, attName, null);
    }

    @Override
    public String getDownloadUrl(String bucket, String fileId, String attName, String fop) {
        try {
            CloudFileProperties.Bucket cloudBucket = properties.getBucketByname(bucket);
            Assert.notNull(cloudBucket, "未找到名称为" + cloudBucket + "的存储空间配置");
            DownloadUrl downloadUrl = new DownloadUrl(cloudBucket.getDomain(), cloudBucket.getUseHttps(), fileId);
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
    public String getPreviewUrl(String bucket, String fileId, Integer width, Integer height) {
        return getDownloadUrl(bucket, fileId, null, "imageView2/2/w/" + width + "/h/" + height);
    }

    @Override
    public String getPreviewUrl(String bucket, String fileId) {
        return getDownloadUrl(bucket, fileId, null);
    }
}
