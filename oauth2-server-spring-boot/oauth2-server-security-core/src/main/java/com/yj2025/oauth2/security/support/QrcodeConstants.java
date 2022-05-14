package com.yj2025.oauth2.security.support;

public class QrcodeConstants {

    public final static String LOGIN_TYPE_FIELD_NAME = "type";

    public final static String SWITCH_USER_PREFIX_STR = "___ierp_switch_usercode___";

    /**
     * flow scope中的二维码登录随机串key
     */
    public final static String QRCODE_TICKET_KEY = "qrCodeTicket";

    /**
     * redis中qrcode的前缀
     */
    public final static String QRCODE_REDIS_KEY_PREFIX = "CAS:QRCODE:";

    /**
     * 二维码ticket保存在redis中的有效时间 单位: 分钟
     */
    public final static Integer TIMEOUT = 5;
}
