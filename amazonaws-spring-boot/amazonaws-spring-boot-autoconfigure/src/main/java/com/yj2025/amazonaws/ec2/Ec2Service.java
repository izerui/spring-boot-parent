package com.yj2025.amazonaws.ec2;

import com.yj2025.amazonaws.AwsProperties;
import com.yj2025.amazonaws.s3.S3Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface Ec2Service {

    @Deprecated
    Ec2Client getClient();

    final class Default implements Ec2Service {

        /**
         * https://docs.amazonaws.cn/AWSEC2/latest/APIReference/Welcome.html
         */
        private Ec2Client client;

        public static Default createfrom(AwsProperties properties) {
            Default my = new Default();
            my.client = Ec2Client.builder()
                    .region(properties.getRegion())
                    .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()))
                    .build();
            return my;
        }

        @Override
        public Ec2Client getClient() {
            return client;
        }

    }
}
