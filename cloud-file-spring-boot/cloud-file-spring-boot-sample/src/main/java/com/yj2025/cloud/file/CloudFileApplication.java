package com.yj2025.cloud.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class CloudFileApplication implements CommandLineRunner {

    @Autowired
    private CloudFileManager cloudFileManager;

    public static void main(String[] args) {
        SpringApplication.run(CloudFileApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        String filePath = "/Users/serv/Downloads/WechatIMG37.png";
//        String bucket = "sz-yunji-test";
        String bucket = "file-p3-demo001";
        String key = UUID.randomUUID().toString() + ".jpg";
        System.out.println(cloudFileManager.getBucket(false));
        cloudFileManager.upload(bucket, key, filePath);

        System.out.println(cloudFileManager.getPreviewUrl(bucket, key));

        System.out.println(cloudFileManager.getPreviewUrl(bucket, key, 100, 100));

        System.out.println(cloudFileManager.getDownloadUrl(bucket, key, "我们.jpg", "imageView2/2/w/100/h/100"));

        System.out.println(cloudFileManager.getDownloadUrl(bucket, key, "我们.jpg"));
    }
}
