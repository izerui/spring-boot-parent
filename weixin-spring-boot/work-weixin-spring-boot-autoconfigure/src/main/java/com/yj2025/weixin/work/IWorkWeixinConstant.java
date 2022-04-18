package com.yj2025.weixin.work;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface IWorkWeixinConstant {
    String CORPID_KEY = "work:weixin:corpId:%s";
    String CORPSECRET_KEY = "work:weixin:corpSecret:%s";
    String TOKEN_KEY = "work:weixin:token:%s";
    String ENCODINGAESKEY_KEY = "work:weixin:encodingAESKey:%s";
    String AGENTID_KEY = "work:weixin:agentId:%s";
    String MSGAUDITLIBPATH_KEY = "work:weixin:msgAuditLibPath:%s";
    String JSAPITICKET_KEY = "work:weixin:jsapiTicket:%s";
    String AGENTJSAPITICKET_KEY = "work:weixin:agentJsapiTicket:%s";
    String ACCESSTOKEN_KEY = "work:weixin:accessToken:%s";
    String OAUTH2REDIRECTURI_KEY = "work:weixin:oauth2redirectUri:%s";
    String WEBHOOKKEY_KEY = "work:weixin:webhookKey:%s";

    default String $_(String format, String value) {
        return String.format(format, value);
    }
}
