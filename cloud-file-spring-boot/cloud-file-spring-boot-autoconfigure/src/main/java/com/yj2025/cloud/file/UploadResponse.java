package com.yj2025.cloud.file;

import lombok.Data;

@Data
public class UploadResponse {

    private String bucket;
    private String key;
    private String hash;
    private Integer fileSize;
    private String fileName;
    private String filePrefix;
    private String mimeType;
    private String ext;
    private String imageInfo;
}
