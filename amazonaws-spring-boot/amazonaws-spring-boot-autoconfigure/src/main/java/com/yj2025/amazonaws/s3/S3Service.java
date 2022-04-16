package com.yj2025.amazonaws.s3;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface S3Service {

    /**
     * 上传文件到s3
     * @param bucket
     * @param key
     * @param requestBody
     * @return
     */
    UploadPartResponse upload(String bucket, String key, RequestBody requestBody);

    /**
     * 从s3获取文件
     * @param bucket
     * @param key
     * @return
     */
    ResponseInputStream<GetObjectResponse> getObject(String bucket, String key);

    @Deprecated
    S3Client getClient();

    final class Default implements S3Service {

        /**
         * https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/Welcome.html
         */
        private S3Client client;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = S3Client.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            return my;
        }

        @Override
        public S3Client getClient() {
            return client;
        }

        @Override
        public UploadPartResponse upload(String bucket, String key, RequestBody requestBody) {
            return client.uploadPart(builder -> builder.bucket(bucket).key(key), requestBody);
        }

        @Override
        public ResponseInputStream<GetObjectResponse> getObject(String bucket, String key) {
            return client.getObject(builder -> builder.bucket(bucket).key(key));
        }
    }
}
