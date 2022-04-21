package com.yj2025.amazonaws.ecr;

import com.yj2025.amazonaws.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.ecr.EcrClient;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface EcrService {

    @Deprecated
    EcrClient getClient();


    final class Default implements EcrService {

        /**
         * https://docs.aws.amazon.com/zh_cn/AmazonECR/latest/APIReference/Welcome.html
         */
        private EcrClient client;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = EcrClient.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            return my;
        }

        @Override
        public EcrClient getClient() {
            return client;
        }
    }
}
