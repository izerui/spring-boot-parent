package com.yj2025.oauth2.server.security.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yj2025.oauth2.security.support.QrcodeConstants.QRCODE_REDIS_KEY_PREFIX;
import static com.yj2025.oauth2.security.support.QrcodeConstants.TIMEOUT;

public class QrcodeService {

    private StringRedisTemplate redisTemplate;
    private ObjectMapper objectMapper;

    public QrcodeService(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 创建一个2分钟有效的用来登录的二维码
     */
    public String createQrcode() {
        String qrCodeTicket;
        while (true) {
            qrCodeTicket = "sso" + RandomStringUtils.randomAlphanumeric(30);
            if (redisTemplate.hasKey(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket)) {
                continue;
            }
            //生成登录ticket对应的缓存key,2分钟有效 , key存在则有效,key不存在表示该ticket失效
            redisTemplate.boundValueOps(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket).set("unknown", TIMEOUT, TimeUnit.MINUTES);
            break;
        }
        return qrCodeTicket;
    }

    /**
     * 扫码后绑定登录账号到当前qrCodeTicket
     *
     * @param qrCodeTicket
     * @param accountName
     */
    public void bindAccountName(String qrCodeTicket, String accountName) {
        //生成登录ticket对应的缓存key,2分钟有效 , key存在则有效,key不存在表示该ticket失效
        redisTemplate.boundValueOps(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket).set(accountName, TIMEOUT, TimeUnit.MINUTES);
    }

    /**
     * 获取二维码的扫码状态
     *
     * @param qrCodeTicket
     * @return
     */
    public QrcodeStatus getQrcodeStatus(String qrCodeTicket) {
        if (qrCodeTicket == null) {
            return new QrcodeStatus(-2, "二维码已失效");
        }
        String value = redisTemplate.boundValueOps(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket).get();
        if (value == null || "".equals(value)) {
            return new QrcodeStatus(-2, "无效的二维码");
        } else if (value.equals("unknown")) {
            return new QrcodeStatus(-1, "待扫描的二维码");
        } else {
            try {
                JsonNode jsonNode = objectMapper.readValue(value, JsonNode.class);
                String accountName = jsonNode.path("accountName").asText();
                String accountPic = jsonNode.path("accountPic").asText();
                String entCode = jsonNode.path("entCode").asText();
                boolean confirm = jsonNode.path("confirm").asBoolean();
                if (confirm) {
                    return new QrcodeStatus(1, accountName, accountPic, "确认登录", entCode);
                } else {
                    return new QrcodeStatus(0, accountName, accountPic, "未确认登录,用来显示头像");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new QrcodeStatus(-3, "未知错误");
        }
    }


    public Map<String, Object> getQrCodeMapValue(String qrCodeTicket) {
        String value = redisTemplate.boundValueOps(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket).get();
        redisTemplate.delete(QRCODE_REDIS_KEY_PREFIX + qrCodeTicket);
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}