package com.yj2025.weixin.work.impl;

import java.util.function.Function;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface KeyConstants {
    Function<String, String> CORPID_KEY = s -> String.format("work:weixin:corpId:%s", s);
    Function<String, String> CORPSECRET_KEY = s -> String.format("work:weixin:corpSecret:%s", s);
    Function<String, String> TOKEN_KEY = s -> String.format("work:weixin:token:%s", s);
    Function<String, String> ENCODINGAESKEY_KEY = s -> String.format("work:weixin:encodingAESKey:%s", s);
    Function<String, String> AGENTID_KEY = s -> String.format("work:weixin:agentId:%s", s);
    Function<String, String> REPLACE_AGENTID_KEY = s -> s.replace("work:weixin:agentId:", "");
    Function<String, String> MSGAUDITLIBPATH_KEY = s -> String.format("work:weixin:msgAuditLibPath:%s", s);
    Function<String, String> JSAPITICKET_KEY = s -> String.format("work:weixin:jsapiTicket:%s", s);
    Function<String, String> AGENTJSAPITICKET_KEY = s -> String.format("work:weixin:agentJsapiTicket:%s", s);
    Function<String, String> ACCESSTOKEN_KEY = s -> String.format("work:weixin:accessToken:%s", s);
    Function<String, String> OAUTH2REDIRECTURI_KEY = s -> String.format("work:weixin:oauth2redirectUri:%s", s);
    Function<String, String> WEBHOOKKEY_KEY = s -> String.format("work:weixin:webhookKey:%s", s);
}
