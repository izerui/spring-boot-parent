package com.yj2025.open.gateway.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 签名工具类
 *
 * @author liuyuhua
 */
public final class SignUtils {

    /**
     * 签名的有效时长（毫秒）
     */
    private final static long EFFICIENT_MILLIS_FOR_SIGN = 1000 * 300;


    /**
     * 签名
     *
     * @param accessToken
     * @param clientSecret
     * @return
     */
    public static String encodeSign(String accessToken, long unixTimestamp, String clientSecret) {
        TreeMap treeMap = new TreeMap();
        treeMap.put("accessToken", accessToken);
        treeMap.put("unixTimestamp", unixTimestamp);
        return encodeSign(treeMap, clientSecret);
    }

    /**
     * 签名
     *
     * @param map    要签名的键值对
     * @param secret 秘钥
     * @return
     */
    public static String encodeSign(Map map, String secret) {
        if (StringUtils.isEmpty(secret)) {
            throw new RuntimeException("签名密钥key不能为空");
        }

        TreeMap treeMap;
        if (map.getClass().isAssignableFrom(TreeMap.class)) {
            treeMap = (TreeMap) map;
        } else {
            treeMap = new TreeMap();
            treeMap.putAll(map);
        }

        Set<Map.Entry> entries = treeMap.entrySet();
        Iterator<Map.Entry> iterator = entries.iterator();
        List<String> values = new ArrayList<>();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String k = String.valueOf(entry.getKey());
            String v = entry.getValue() == null ? null : String.valueOf(entry.getValue());
            if (StringUtils.isNotEmpty(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                values.add(k + "=" + v);
            }
        }
        values.add("key=" + secret);
        String sign = StringUtils.join(values, "&");
        return DigestUtils.md5Hex(sign).toUpperCase();
    }

    public static boolean verify(String accessToken, long unixTimestamp, String sign, String clientSecret) {
        long difference = System.currentTimeMillis() - unixTimestamp;
        difference = Math.abs(difference);
        Assert.state(difference <= EFFICIENT_MILLIS_FOR_SIGN, "签名时间戳已过期，请使用最新时间戳重试!");
        String encodeSign = encodeSign(accessToken, unixTimestamp, clientSecret);
        return StringUtils.equals(encodeSign, sign);
    }

}
