package com.yj2025.cloud.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationStatus;
import com.qiniu.storage.model.FileInfo;
import com.yj2025.commons.vo.AttachmentVO;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface CloudFileProductManager {

    /**
     * 根据文件名生成随机的一个key
     *
     * @param productId 产品线
     * @param fileName  文件名
     * @return
     */
    String generateKey(String productId, String fileName);

    /**
     * 转换返回内容到 AttachmentVO
     *
     * @param productId 产品线
     * @param response
     * @return
     */
    AttachmentVO convert(String productId, UploadResponse response) throws JsonProcessingException;

    /**
     * 获取空间中文件的属性
     *
     * @param productId 产品线
     * @param bucket    空间名称
     * @param key       key
     * @return 文件属性
     * @link http://developer.qiniu.com/kodo/api/stat
     */
    FileInfo getFileInfo(String productId, String bucket, String key);

    /**
     * 获取空间中文件的属性
     *
     * @param productId 产品线
     * @param isPublic  是否公共桶
     * @param key       key
     * @return 文件属性
     * @link http://developer.qiniu.com/kodo/api/stat
     */
    FileInfo getFileInfo(String productId, boolean isPublic, String key);

    /**
     * 通过公共或者私有获取桶配置
     *
     * @param productId 产品线
     * @param isPublic  true：公共桶 FALSE：私有桶
     * @return
     */
    CloudFileProperties.Bucket getBucket(String productId, boolean isPublic);

    /**
     * 通过桶名字获取桶配置
     *
     * @param productId 产品线
     * @param bucket    桶名称
     * @return
     */
    CloudFileProperties.Bucket getBucket(String productId, String bucket);

    /**
     * 生成上传token
     *
     * @param productId 产品线
     * @param bucket    空间名
     * @param key       key，可为 null
     * @return 生成的上传token
     */
    String getUploadToken(String productId, String bucket, String key);

    /**
     * 生成上传token
     *
     * @param productId 产品线
     * @param isPublic  是否公共桶
     * @param key       key，可为 null
     * @return 生成的上传token
     */
    String getUploadToken(String productId, boolean isPublic, String key);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param bucket    目标桶
     * @param key       key
     * @param bytes     待上传的字节数组
     */
    UploadResponse upload(String productId, String bucket, String key, byte[] bytes);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param bytes     待上传的字节数组
     */
    UploadResponse upload(String productId, boolean isPublic, String key, byte[] bytes);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param bucket    目标桶
     * @param key       key
     * @param file      待上传文件
     */
    UploadResponse upload(String productId, String bucket, String key, File file);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param file      待上传文件
     */
    UploadResponse upload(String productId, boolean isPublic, String key, File file);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param bucket    目标桶
     * @param key       key
     * @param filePath  待上传文件路径
     */
    UploadResponse upload(String productId, String bucket, String key, String filePath);

    /**
     * 自动生成token并上传
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param filePath  待上传文件路径
     */
    UploadResponse upload(String productId, boolean isPublic, String key, String filePath);

    /**
     * 自动生成token并上传
     *
     * @param productId   产品线
     * @param bucket      目标桶
     * @param key         key
     * @param inputStream 待上传文件输入流
     * @param mime        指定文件mimetype [可选]
     */
    UploadResponse upload(String productId, String bucket, String key, InputStream inputStream, String mime);

    /**
     * 自动生成token并上传
     *
     * @param productId   产品线
     * @param isPublic    是否是公共桶
     * @param key         key
     * @param inputStream 待上传文件输入流
     * @param mime        指定文件mimetype [可选]
     */
    UploadResponse upload(String productId, boolean isPublic, String key, InputStream inputStream, String mime);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param bucket    桶
     * @param key       key
     * @param attName   下载指定的文件名 未指定则以预览形式打开
     * @return 下载地址
     */
    String getDownloadUrl(String productId, String bucket, String key, String attName);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param attName   下载指定的文件名 未指定则以预览形式打开
     * @return 下载地址
     */
    String getDownloadUrl(String productId, boolean isPublic, String key, String attName);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param bucket    桶
     * @param key       key
     * @param attName   下载指定的文件名 未指定则以预览形式打开
     * @param fop       fop配置 例如：imageView/2/w/640/h/960 [可选]
     * @return 下载地址
     */
    String getDownloadUrl(String productId, String bucket, String key, String attName, String fop);

    /**
     * 生成文件下载url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param attName   下载指定的文件名 未指定则以预览形式打开
     * @param fop       fop配置 例如：imageView/2/w/640/h/960 [可选]
     * @return 下载地址
     */
    String getDownloadUrl(String productId, boolean isPublic, String key, String attName, String fop);

    /**
     * 参考: http://78re52.com0.z0.glb.qiniucdn.com/docs/v6/api/reference/fop/image/imageview2.html
     * 生成图片预览url（等比缩放）,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param bucket    桶
     * @param key       key
     * @param width     限定宽
     * @param height    限定高
     * @return 下载地址
     */
    String getPreviewUrl(String productId, String bucket, String key, Integer width, Integer height);

    /**
     * 参考: http://78re52.com0.z0.glb.qiniucdn.com/docs/v6/api/reference/fop/image/imageview2.html
     * 生成图片预览url（等比缩放）,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @param width     限定宽
     * @param height    限定高
     * @return 下载地址
     */
    String getPreviewUrl(String productId, boolean isPublic, String key, Integer width, Integer height);


    /**
     * 获取源图片预览url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param bucket    桶
     * @param key       key
     * @return 图片地址
     */
    String getPreviewUrl(String productId, String bucket, String key);

    /**
     * 获取源图片预览url,私有空间的话有效时长5分钟
     *
     * @param productId 产品线
     * @param isPublic  是否是公共桶
     * @param key       key
     * @return 图片地址
     */
    String getPreviewUrl(String productId, boolean isPublic, String key);

    /**
     * 重命名空间中的文件，可以设置force参数为true强行覆盖空间已有同名文件
     *
     * @param productId  产品线
     * @param bucket     空间名称
     * @param oldFileKey 文件名称
     * @param newFileKey 新文件名
     * @param force      强制覆盖空间中已有同名（和 newFileKey 相同）的文件
     */
    Response rename(String productId, String bucket, String oldFileKey, String newFileKey, boolean force);

    /**
     * 删除指定空间、文件名的文件
     *
     * @param productId 产品线
     * @param bucket    空间名称
     * @param key       文件key
     */
    Response delete(String productId, String bucket, String key);

    /**
     * 批量删除指定空间、文件名的文件
     * 单次批量请求的文件数量不得超过1000
     *
     * @param productId 产品线
     * @param bucket    空间名称
     * @param keys      文件key集合
     */
    Response batchDelete(String productId, String bucket, List<String> keys);

    /**
     * 批量重命名
     * 单次批量请求的文件数量不得超过1000
     *
     * @param productId 产品线
     * @param bucket    空间名称
     * @param keyMap    key为文件 fileKey,value 为 新文件key
     */
    Response batchRename(String productId, String bucket, Map<String, String> keyMap);

    /**
     * 打包压缩
     *
     * @param productId 产品线
     * @param bucket    空间名称
     * @param zipName   压缩包名称
     * @param zipTxt    打包文档，详见：https://developer.qiniu.com/dora/1667/mkzip
     * @param notifyUrl 回调地址
     */
    OperationStatus mkzip(String productId, String bucket, String zipName, String zipTxt, String notifyUrl);
}
