package com.yj2025.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class AttachmentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String recordId;

    /**
     * 文件在七牛上的key
     */
    private String key;
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
    @JsonProperty("eTag")
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
        return StringUtils.isNotBlank(url) ? url : this.previewUrl;
    }

    /**
     * 老的文件key对应现在的key
     */
    @Deprecated
    private String fileId;
    /**
     * 老的tag，对应现在的eTag
     */
    @Deprecated
    private String tag;
    /**
     * 原来的url，对应现在的previewUrl，downloadUrl
     */
    @Deprecated
    private String url;

    public String getKey() {
        return StringUtils.isNotBlank(key) ? key : this.fileId;
    }

    public String getETag() {
        return StringUtils.isNotBlank(eTag) ? eTag : this.tag;
    }

    public String getPreviewUrl() {
        return StringUtils.isNotBlank(previewUrl) ? previewUrl : this.url;
    }

    public String getDownloadUrl() {
        return StringUtils.isNotBlank(downloadUrl) ? downloadUrl : this.url;
    }

    public Boolean getPrivateBucket() {
        if (StringUtils.isNotBlank(getPreviewUrl())) {
            return getPreviewUrl().startsWith("https://pfile.") //线上
                    || getPreviewUrl().startsWith("https://pdfile.") //开发
                    || getPreviewUrl().startsWith("https://ptfile.");//测试
        }
        return true;
    }
}
