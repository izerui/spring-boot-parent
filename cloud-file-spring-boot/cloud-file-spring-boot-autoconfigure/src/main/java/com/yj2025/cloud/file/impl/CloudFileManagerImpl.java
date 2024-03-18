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
import com.yj2025.commons.vo.AttachmentVO;
import lombok.SneakyThrows;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

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
    public String generateKey(String fileName) {
        String key = fileName.replaceAll("^.+?(\\.\\w*)??$", UUID.randomUUID().toString() + "$1");
        return key;
    }

    @SneakyThrows
    @Override
    public AttachmentVO convert(UploadResponse response) {
        AttachmentVO attachmentVO = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(response), AttachmentVO.class);
        CloudFileProperties.Bucket bucket = getBucket(attachmentVO.getBucket());
        attachmentVO.setPrivateBucket(!bucket.getIsPublic());
        return attachmentVO;
    }

    @Override
    public FileInfo getFileInfo(String bucket, String key) {
        try {
            return bucketManager.stat(bucket, key);
        } catch (QiniuException e) {
            throw new CloudFileException(e.getMessage(), e);
        }
    }

    @Override
    public FileInfo getFileInfo(boolean isPublic, String key) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getFileInfo(bucket.getBucketName(), key);
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
        // https://developer.qiniu.com/kodo/1235/vars#magicvar
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append("\"bucket\":\"$(bucket)\",")
                .append("\"key\":\"$(key)\",")
                .append("\"eTag\":\"$(etag)\",")
                .append("\"fileSize\":$(fsize),")
                .append("\"fileName\":$(fname),")
                .append("\"filePrefix\":$(fprefix),")
                .append("\"mimeType\":\"$(mimeType)\",")
                .append("\"ext\":\"$(ext)\"")
                .append("}");
        final String returnBody = builder.toString();
        StringMap policy = new StringMap();
        policy.put("returnBody", returnBody);
        return auth.uploadToken(bucket, key, 3600, policy);
    }

    @Override
    public String getUploadToken(boolean isPublic, String key) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getUploadToken(bucket.getBucketName(), key);
    }

    @Override
    public UploadResponse upload(String bucket, String key, byte[] bytes) {
        try {
            String token = getUploadToken(bucket, key);
            Response response = this.uploadManager.put(bytes, key, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(boolean isPublic, String key, byte[] bytes) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return upload(bucket.getBucketName(), key, bytes);
    }

    @Override
    public UploadResponse upload(String bucket, String key, File file) {
        try {
            String token = getUploadToken(bucket, key);
            Response response = this.uploadManager.put(file, key, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(boolean isPublic, String key, File file) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return upload(bucket.getBucketName(), key, file);
    }

    @Override
    public UploadResponse upload(String bucket, String key, String filePath) {
        try {
            String token = getUploadToken(bucket, key);
            Response response = this.uploadManager.put(filePath, key, token);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(boolean isPublic, String key, String filePath) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return upload(bucket.getBucketName(), key, filePath);
    }

    @Override
    public UploadResponse upload(String bucket, String key, InputStream inputStream, String mime) {
        try {
            String token = getUploadToken(bucket, key);
            Response response = this.uploadManager.put(inputStream, key, token, null, mime);
            return OBJECT_MAPPER.readValue(response.bodyString(), UploadResponse.class);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public UploadResponse upload(boolean isPublic, String key, InputStream inputStream, String mime) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return upload(bucket.getBucketName(), key, inputStream, mime);
    }

    @Override
    public String getDownloadUrl(String bucket, String key, String attName) {
        return getDownloadUrl(bucket, key, attName, null);
    }

    @Override
    public String getDownloadUrl(boolean isPublic, String key, String attName) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getDownloadUrl(bucket.getBucketName(), key, attName);
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
    public String getDownloadUrl(boolean isPublic, String key, String attName, String fop) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getDownloadUrl(bucket.getBucketName(), key, attName, fop);
    }

    @Override
    public String getPreviewUrl(String bucket, String key, Integer width, Integer height) {
        return getDownloadUrl(bucket, key, null, "imageView2/2/w/" + width + "/h/" + height);
    }

    @Override
    public String getPreviewUrl(boolean isPublic, String key, Integer width, Integer height) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getPreviewUrl(bucket.getBucketName(), key, width, height);
    }

    @Override
    public String getPreviewUrl(String bucket, String key) {
        return getDownloadUrl(bucket, key, null);
    }

    @Override
    public String getPreviewUrl(boolean isPublic, String key) {
        CloudFileProperties.Bucket bucket = getBucket(isPublic);
        return getPreviewUrl(bucket.getBucketName(), key);
    }

    @Override
    public Response rename(String bucket, String oldFileKey, String newFileKey, boolean force) {
        try {
            return bucketManager.rename(bucket, oldFileKey, newFileKey, force);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }

    @Override
    public Response delete(String bucket, String key) {
        try {
            return bucketManager.delete(bucket, key);
        } catch (Exception ex) {
            throw new CloudFileException(ex.getMessage(), ex);
        }
    }
}
