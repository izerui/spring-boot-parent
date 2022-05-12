package com.yj2025.amazonaws;

import com.yj2025.amazonaws.ec2.Ec2Service;
import com.yj2025.amazonaws.ecr.EcrService;
import com.yj2025.amazonaws.s3.S3Service;
import com.yj2025.amazonaws.sts.StsService;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public interface AwsService {

    S3Service getS3Service();

    Ec2Service getEc2Service();

    EcrService getEcrService();

    StsService getStsService();

}
