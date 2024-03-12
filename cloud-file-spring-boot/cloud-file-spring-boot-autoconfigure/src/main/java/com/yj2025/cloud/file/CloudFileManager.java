package com.yj2025.cloud.file;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileInfo;
import com.yj2025.commons.vo.AttachmentVO;

import java.io.File;
import java.io.InputStream;

public interface CloudFileManager {

    /**
     * 根据文件名生成随机的一个key
     *
     * @param fileName 文件名
     * @return
     */
    String generateKey(String fileName);

    /**
     * 转换返回内容到 AttachmentVO
     * @param response
     * @return
     */
    AttachmentVO convert(UploadResponse response);

    /**
     * 获取空间中文件的属性
     *
     * @param bucket 空间名称
     * @param key    key
     * @return 文件属性
     * @link http://developer.qiniu.com/kodo/api/stat
     */
    FileInfo getFileInfo(String bucket, String key);

    /**
     * 获取空间中文件的属性
     *
     * @param isPublic 是否公共桶
     * @param key      key
     * @return 文件属性
     * @link http://developer.qiniu.com/kodo/api/stat
     */
    FileInfo getFileInfo(boolean isPublic, String key);

    /**
     * 通过公共或者私有获取桶配置
     *
     * @param isPublic true：公共桶 FALSE：私有桶
     * @return
     */
    CloudFileProperties.Bucket getBucket(boolean isPublic);

    /**
     * 通过桶名字获取桶配置
     *
     * @param bucket 桶名称
     * @return
     */
    CloudFileProperties.Bucket getBucket(String bucket);

    /**
     * 生成上传token
     *
     * @param bucket 空间名
     * @param key    key，可为 null
     * @return 生成的上传token
     */
    String getUploadToken(String bucket, String key);

    /**
     * 生成上传token
     *
     * @param isPublic 是否公共桶
     * @param key      key，可为 null
     * @return 生成的上传token
     */
    String getUploadToken(boolean isPublic, String key);

    /**
     * 自动生成token并上传
     *
     * @param bucket 目标桶
     * @param key    key
     * @param bytes  待上传的字节数组
     */
    UploadResponse upload(String bucket, String key, byte[] bytes);

    /**
     * 自动生成token并上传
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param bytes    待上传的字节数组
     */
    UploadResponse upload(boolean isPublic, String key, byte[] bytes);

    /**
     * 自动生成token并上传
     *
     * @param bucket 目标桶
     * @param key    key
     * @param file   待上传文件
     */
    UploadResponse upload(String bucket, String key, File file);

    /**
     * 自动生成token并上传
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param file     待上传文件
     */
    UploadResponse upload(boolean isPublic, String key, File file);

    /**
     * 自动生成token并上传
     *
     * @param bucket   目标桶
     * @param key      key
     * @param filePath 待上传文件路径
     */
    UploadResponse upload(String bucket, String key, String filePath);

    /**
     * 自动生成token并上传
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param filePath 待上传文件路径
     */
    UploadResponse upload(boolean isPublic, String key, String filePath);

    /**
     * 自动生成token并上传
     *
     * @param bucket      目标桶
     * @param key         key
     * @param inputStream 待上传文件输入流
     * @param mime        指定文件mimetype [可选]
     */
    UploadResponse upload(String bucket, String key, InputStream inputStream, String mime);

    /**
     * 自动生成token并上传
     *
     * @param isPublic    是否是公共桶
     * @param key         key
     * @param inputStream 待上传文件输入流
     * @param mime        指定文件mimetype [可选]
     */
    UploadResponse upload(boolean isPublic, String key, InputStream inputStream, String mime);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param bucket  桶
     * @param key     key
     * @param attName 下载指定的文件名 未指定则以预览形式打开
     * @return 下载地址
     */
    String getDownloadUrl(String bucket, String key, String attName);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param attName  下载指定的文件名 未指定则以预览形式打开
     * @return 下载地址
     */
    String getDownloadUrl(boolean isPublic, String key, String attName);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param bucket  桶
     * @param key     key
     * @param attName 下载指定的文件名 未指定则以预览形式打开
     * @param fop     fop配置 例如：imageView/2/w/640/h/960 [可选]
     * @return 下载地址
     */
    String getDownloadUrl(String bucket, String key, String attName, String fop);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param attName  下载指定的文件名 未指定则以预览形式打开
     * @param fop      fop配置 例如：imageView/2/w/640/h/960 [可选]
     * @return 下载地址
     */
    String getDownloadUrl(boolean isPublic, String key, String attName, String fop);

    /**
     * 参考: http://78re52.com0.z0.glb.qiniucdn.com/docs/v6/api/reference/fop/image/imageview2.html
     * 生成图片预览url（等比缩放）,私有空间的话有效时长5分钟
     *
     * @param bucket 桶
     * @param key    key
     * @param width  限定宽
     * @param height 限定高
     * @return 下载地址
     */
    String getPreviewUrl(String bucket, String key, Integer width, Integer height);

    /**
     * 参考: http://78re52.com0.z0.glb.qiniucdn.com/docs/v6/api/reference/fop/image/imageview2.html
     * 生成图片预览url（等比缩放）,私有空间的话有效时长5分钟
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @param width    限定宽
     * @param height   限定高
     * @return 下载地址
     */
    String getPreviewUrl(boolean isPublic, String key, Integer width, Integer height);


    /**
     * 获取源图片预览url,私有空间的话有效时长5分钟
     *
     * @param bucket 桶
     * @param key    key
     * @return 图片地址
     */
    String getPreviewUrl(String bucket, String key);

    /**
     * 获取源图片预览url,私有空间的话有效时长5分钟
     *
     * @param isPublic 是否是公共桶
     * @param key      key
     * @return 图片地址
     */
    String getPreviewUrl(boolean isPublic, String key);

    /**
     * 创建桶
     *
     * @param isPublic  是否是公共桶
     * @param bucketName 桶名称
     */
    void createBucket(boolean isPublic,String bucketName) throws QiniuException;
}
