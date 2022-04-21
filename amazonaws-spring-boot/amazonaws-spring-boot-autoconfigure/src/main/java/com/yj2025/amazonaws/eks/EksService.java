package com.yj2025.amazonaws.eks;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.eks.EksClient;

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
     *
     * @return
     */
    String getOauthToken(String eksClusterName);

    @Deprecated
    EksClient getClient();

    final class Default implements EksService {

        /**
         * https://docs.aws.amazon.com/zh_cn/eks/latest/APIReference/Welcome.html
         */
        private EksClient client;
        private AwsProperties properties;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.properties = properties;
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
        public String getOauthToken(String eksClusterName) {
            try {
                URI uri = new URI("https", String.format("sts.%s.amazonaws.com.cn", properties.getRegion().id()), "/", null);
                SdkHttpFullRequest requestToSign = SdkHttpFullRequest
                        .builder()
                        .method(SdkHttpMethod.GET)
                        // https://docs.amazonaws.cn/aws/latest/userguide/endpoints-Ningxia.html
                        .uri(uri)
                        .appendHeader("x-k8s-aws-id", eksClusterName)
                        .appendRawQueryParameter("Action", "GetCallerIdentity")
                        .appendRawQueryParameter("Version", "2011-06-15")
                        .build();

                ZoneId zoneId = ZoneId.of("UTC");
                ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), zoneId);
                ZonedDateTime expirationDate = ZonedDateTime.ofInstant(now.plusSeconds(60).toInstant(), zoneId);
                Aws4PresignerParams presignerParams = Aws4PresignerParams.builder()
                        .awsCredentials(AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                        .signingRegion(properties.getRegion())
                        .signingName("sts")
                        .expirationTime(expirationDate.toInstant())
                        .build();
                SdkHttpFullRequest signedRequest = Aws4Signer.create().presign(requestToSign, presignerParams);
                String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(signedRequest.getUri().toString().getBytes(StandardCharsets.UTF_8));
                return ("k8s-aws-v1." + encodedUrl);
            } catch (Exception e) {
                String errorMessage = "A problem occurred generating an Eks authentication token for cluster: " + eksClusterName;
                throw new RuntimeException(errorMessage, e);
            }
        }
    }
}
