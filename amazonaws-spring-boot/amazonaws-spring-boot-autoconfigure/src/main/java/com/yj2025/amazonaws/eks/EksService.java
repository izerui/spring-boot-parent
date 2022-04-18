package com.yj2025.amazonaws.eks;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;

/**
 * @author liuyuhua
 * @date 2022/4/16
 */
public interface EksService {

    /**
     * 获取请求token
     * @return
     */
    String getOauthToken();

    @Deprecated
    EksClient getClient();

    final class Default implements EksService {

        /**
         * https://docs.aws.amazon.com/zh_cn/eks/latest/APIReference/Welcome.html
         */
        private EksClient client;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = EksClient.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            return my;
        }

        @Override
        public EksClient getClient() {
            return client;
        }

        @Override
        public String getOauthToken() {
            //
            return null;
        }
    }
}
