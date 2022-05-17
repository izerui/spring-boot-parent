package com.yj2025.oauth2.security.support;

import java.util.function.Function;

public class QrcodeConstants {

    /**
     * flow scope中的二维码登录随机串key
     */
    public final static String QRCODE_TICKET_KEY = "qrCodeTicket";

    /**
     * redis中qrcode的前缀
     */
    public final static Function<String, String> QRCODE_REDIS_KEY_PREFIX = applicationName -> {
        if (applicationName == null) {
            return "CAS:QRCODE:";
        } else {
            return applicationName + ":QRCODE:";
        }
    };

    /**
     * 二维码ticket保存在redis中的有效时间 单位: 分钟
     */
    public final static Integer TIMEOUT = 5;
}
