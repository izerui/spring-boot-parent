package com.yj2025.amazonaws.s3;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

/**
 * // 海外：https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/RESTAuthentication.html
 * // 中国：https://docs.amazonaws.cn/AmazonS3/latest/userguide/RESTAuthentication.html
 *
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface S3Service {

    /**
     * 上传文件到s3
     *
     * @param bucket
     * @param key
     * @param requestBody
     * @return
     */
    UploadPartResponse upload(String bucket, String key, RequestBody requestBody);

    /**
     * 从s3获取文件
     *
     * @param bucket
     * @param key
     * @return
     */
    ResponseInputStream<GetObjectResponse> getObject(String bucket, String key);


    /**
     * 获取预签章的URL
     *
     * @param bucket         桶
     * @param key            路径
     * @param expiredMinutes 失效时长(分钟)
     * @return
     */
    URL getPresignedUrl(String bucket, String key, int expiredMinutes);


    @Deprecated
    S3Client getClient();

    final class Default implements S3Service {

        /**
         * https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/Welcome.html
         */
        private S3Client client;
        private S3Presigner presigner;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = S3Client.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            my.presigner = S3Presigner.builder()
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

        /**
         * 旧版sdk： https://docs.amazonaws.cn/AmazonS3/latest/userguide/ShareObjectPreSignedURL.html
         * 新版sdk：https://docs.aws.amazon.com/zh_tw/sdk-for-java/latest/developer-guide/examples-s3-presign.html
         *
         * @param bucket
         * @param key
         * @return
         */
        @Override
        public URL getPresignedUrl(String bucket, String key, int expiredMinutes) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expiredMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    presigner.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url();
        }
    }
}
