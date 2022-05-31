package com.yj2025.open.commons;

/**
 * @author liuyuhua
 */
public class Constants {
    /**
     * accessToken 有效时长（毫秒）
     */
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 7200;
    /**
     * accessToken scope
     */
    public static final String ACCESS_TOKEN_SCOPES = "SCOPE";
    /**
     * accessToken 授权类型
     */
    public static final String ACCESS_TOKEN_GRANTTYPES = "client_credentials";
    /**
     * clientId的secret缓存有效时长
     */
    public static final int CLIENT_SECRET_VALIDITY_SECONDS = 7500;
    /**
     * 客户端ID字段名称
     */
    public static final String CLIENT_ID_FIELDNAME = "client_id";
    /**
     * 客户端密钥字段名称
     */
    public static final String CLIENT_SECRET_FIELDNAME = "client_secret";
    /**
     * 授权类型字段名称
     */
    public static final String GRANT_TYPE_FIELDNAME = "grant_type";
    /**
     * 租户ID字段名称
     */
    public static final String TENANT_ID_FIELDNAME = "tenant_id";
    /**
     * oauth2服务器获取token地址
     */
    public static final String OAUTH2_TOKEN_URL = "http://open-oauth/oauth/token";
    /**
     * 鉴权请求接口header传入的时间戳参数名
     */
    public static final String HEADER_UNIX_TIMESTAMP_FIELDNAME = "UnixTimestamp";
    /**
     * 鉴权请求接口header传入的认证信息参数名
     */
    public static final String HEADER_AUTHORIZATION_FIELDNAME = "Authorization";
    /**
     * 鉴权请求接口header传入的签名参数名
     */
    public static final String HEADER_SIGN_FIELDNAME = "Sign";
    /**
     * jws证书名字
     */
    public static final String JWT_SSL_FILENAME = "jwt.jks";
    /**
     * jws证书alias名字
     */
    public static final String JWT_SSL_ALIAS_FILENAME = "jwt";
    /**
     * jwt证书密码
     */
    public static final String JWT_SSL_PASSWORD = "123456";
}
