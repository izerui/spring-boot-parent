package com.yj2025.amazonaws.sts;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

/**
 * @author liuyuhua
 * @date 2022/4/16
 */
public interface StsService {

    /**
     * 返回IAM的临时安全凭证
     * @param durationSeconds
     * @return
     */
    GetSessionTokenResponse getStsToken(Integer durationSeconds);

    @Deprecated
    StsClient getClient();

    final class Default implements StsService {

        /**
         * https://docs.aws.amazon.com/zh_cn/IAM/latest/UserGuide/id_credentials_temp_request.html
         */
        private StsClient client;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = StsClient.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            return my;
        }

        @Override
        public StsClient getClient() {
            return client;
        }

        @Override
        public GetSessionTokenResponse getStsToken(Integer durationSeconds) {
            return client.getSessionToken(builder -> builder.durationSeconds(durationSeconds));
        }
    }
}
