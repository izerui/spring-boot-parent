package com.yj2025.commons.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttachmentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    private String fileId;
    /**
     * 文件存储桶
     */
    private String bucket;
    /**
     * 是否是私有桶
     */
    private Boolean privateBucket;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小
     */
    private Double fileSize;
    /**
     * 文件类型
     */
    private String mimeType;
    /**
     * 文件后缀
     */
    private String ext;
    /**
     * 文件etag
     */
    private String eTag;
    /**
     * 文件etag
     */
    private String hash;
    /**
     * 文件预览地址
     */
    private String previewUrl;
    /**
     * 文件下载地址
     */
    private String downloadUrl;
    /**
     * 备注
     */
    private String remark;

    /**
     * 是否是主图
     */
    private Boolean isHome;

    /**
     * 同文件预览地址
     *
     * @return
     */
    public String getUrl() {
        return previewUrl;
    }

    /**
     * 同 fileId
     * @return
     */
    public String getKey() {
        return fileId;
    }

}
