package com.yj2025.oauth2.security.support;

public class MappingUrls {
    /**
     * 获取token地址
     */
    public final static String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * 刷新token(切换账套)地址
     */
    public final static String OAUTH_REFRESH_URL = "/oauth/refresh";

    /**
     * 登出、注销地址
     */
    public final static String OAUTH_REVOKE_URL = "/oauth/revoke";

    /**
     * 验证TOKEN，并返回authentication信息地址, 仅网关在rest模式下访问
     */
    public final static String OAUTH_CHECK_TOKEN_URL = "/oauth/check_token";

    /**
     * 暴露给第三方获取jwt公钥，用来验签(云集本身不用)
     */
    public final static String TOKEN_KEY_URL = "/oauth/token_key";

    /**
     * 生成登录二维码地址
     */
    public final static String QRCODE_GENERATE_URL = "/qrcode/generate";

    /**
     * 扫码解析的重定向URL地址
     */
    public final static String QRCODE_REDIRECT_URL = "/qrcode/redirect";

    /**
     * 扫码状态检查地址
     */
    public final static String QRCODE_VALIDATE_URL = "/qrcode/validate";

    /**
     * jwt 证书key地址, 仅网关在jwt模式下访问
     */
    public final static String JWT_RSA_KEY_URL = "/rsa/key";

    /**
     * 认证服务器默认放开访问权限的地址集合
     */
    public final static String[] OAUTH_SERVER_IGNORE_URLS = {
            OAUTH_REVOKE_URL,
            OAUTH_TOKEN_URL,
            TOKEN_KEY_URL,
            OAUTH_CHECK_TOKEN_URL,
            JWT_RSA_KEY_URL,
            QRCODE_GENERATE_URL,
            QRCODE_VALIDATE_URL
    };


    /**
     * 网关URL地址白名单，无须权限可放开访问的URL地址集合
     */
    public final static String[] GATEWAY_IGNORE_URLS = {
            OAUTH_TOKEN_URL,
            OAUTH_REFRESH_URL,
            TOKEN_KEY_URL,
            QRCODE_GENERATE_URL,
            QRCODE_REDIRECT_URL,
            QRCODE_VALIDATE_URL
    };


}
