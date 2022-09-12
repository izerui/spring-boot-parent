package com.yj2025.commons.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttachmentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileId;
    private String bucket;
    private Boolean privateBucket;
    private String fileName;
    private Double fileSize;
    private String mimeType;
    private String ext;
    private String hash;
    private String previewUrl;
    private String downloadUrl;
    private String remark;

    public String getUrl() {
        return previewUrl;
    }

}
