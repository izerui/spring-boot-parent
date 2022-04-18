package com.yj2025.amazonaws;

import com.yj2025.amazonaws.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
@SpringBootApplication
public class AwsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AwsApplication.class, args);
    }

    private String basePath = "amazonaws-spring-boot/amazonaws-spring-boot-sample/";

    @Autowired
    private AwsService awsService;

    @Override
    public void run(String... args) throws Exception {
        this.getFile();
        this.uploadFile();
        this.getStsToken();
        this.getPresignedUrl();
    }

    private void getPresignedUrl() {
        URL presignedUrl = awsService.getS3Service().getPresignedUrl("ucloud-bak", "AwsApplication.java");
        System.out.println(presignedUrl.toString());
    }

    private void getFile() throws IOException {
        S3Service s3Service = awsService.getS3Service();
        ResponseInputStream<GetObjectResponse> fetch = s3Service.getObject("www.yunji2025.com", "README.md");
        fetch.transferTo(new FileOutputStream(basePath + "target/README2222.md"));
    }

    private void uploadFile() {
        UploadPartResponse upload = awsService.getS3Service().upload("ucloud-bak", "src/main/java/com/yj2025/amazonaws/AwsApplication.java", RequestBody.fromFile(new File(basePath + "src/main/java/com/yj2025/amazonaws/AwsApplication.java")));
        System.out.println(upload.responseMetadata().toString());
    }

    private void getStsToken() {
        GetSessionTokenResponse stsToken = awsService.getStsService().getStsToken(900);
        String sessionToken = stsToken.credentials().sessionToken();
        System.out.println(sessionToken);
    }
}
