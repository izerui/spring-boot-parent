package com.yj2025.cloud.file;

import lombok.Data;

@Data
public class UploadResponse {

    /**
     * 存储桶
     */
    private String bucket;
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 文件eTag
     */
    private String eTag;
    /**
     * 文件大小
     */
    private Integer fileSize;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件前缀
     */
    private String filePrefix;
    /**
     * 文件类型
     */
    private String mimeType;
    /**
     * 文件后缀
     */
    private String ext;
    /**
     * 图片解析信息
     */
    private String imageInfo;
}
