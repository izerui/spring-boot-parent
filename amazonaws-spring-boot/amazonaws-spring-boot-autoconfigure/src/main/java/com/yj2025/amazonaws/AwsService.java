package com.yj2025.amazonaws;

import com.yj2025.amazonaws.ec2.Ec2Service;
import com.yj2025.amazonaws.ecr.EcrService;
import com.yj2025.amazonaws.s3.S3Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface AwsService {

    S3Service getS3Service();

    Ec2Service getEc2Service();

    EcrService getEcrService();

}
