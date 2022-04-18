package com.yj2025.amazonaws;

import com.yj2025.amazonaws.ec2.Ec2Service;
import com.yj2025.amazonaws.ecr.EcrService;
import com.yj2025.amazonaws.s3.S3Service;
import com.yj2025.amazonaws.sts.StsService;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
public class AwsServiceFactory implements FactoryBean<AwsService> {

    private AwsProperties properties;

    public AwsServiceFactory(AwsProperties properties) {
        this.properties = properties;
    }

    @Override
    public AwsService getObject() throws Exception {
        AwsService awsClient = new AwsService() {
            @Override
            public S3Service getS3Service() {
                return S3Service.Default.createfrom(properties);
            }


            @Override
            public Ec2Service getEc2Service() {
                return Ec2Service.Default.createfrom(properties);
            }

            @Override
            public EcrService getEcrService() {
                return EcrService.Default.createfrom(properties);
            }

            @Override
            public StsService getStsService() {
                return StsService.Default.createfrom(properties);
            }
        };
        return awsClient;
    }

    @Override
    public Class<?> getObjectType() {
        return AwsService.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

}
