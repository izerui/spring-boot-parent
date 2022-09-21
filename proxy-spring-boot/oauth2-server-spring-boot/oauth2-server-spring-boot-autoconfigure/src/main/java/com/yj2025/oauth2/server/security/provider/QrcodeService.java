package com.yj2025.oauth2.server.security.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yj2025.oauth2.server.utils.ExceptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yj2025.oauth2.security.support.QrcodeConstants.TIMEOUT;
import static com.yj2025.oauth2.server.utils.ExceptionUtils.wrapExceptions;

public class QrcodeService {

    private StringRedisTemplate redisTemplate;
    private ObjectMapper objectMapper;
    private String qrcodePrefix;

    public QrcodeService(RedisConnectionFactory redisConnectionFactory, String qrcodePrefix) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        this.qrcodePrefix = qrcodePrefix;
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
            if (redisTemplate.hasKey(qrcodePrefix + qrCodeTicket)) {
                continue;
            }
            //生成登录ticket对应的缓存key,2分钟有效 , key存在则有效,key不存在表示该ticket失效
            redisTemplate.boundValueOps(qrcodePrefix + qrCodeTicket).set("unknown", TIMEOUT, TimeUnit.MINUTES);
            break;
        }
        return qrCodeTicket;
    }

    /**
     * 根据ticket读取redis中的值，并转换成QrcodeStatus对象
     *
     * @param qrCodeTicket
     * @return
     */
    public QrcodeStatus getQrcodeStatus(String qrCodeTicket) {
        if (qrCodeTicket == null) {
            return new QrcodeStatus(-2, "二维码已失效");
        }
        String value = redisTemplate.boundValueOps(qrcodePrefix + qrCodeTicket).get();
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


    /**
     * 根据ticket读取redis中的值，并转换成Map，同时将ticket失效
     *
     * @param qrCodeTicket
     * @return
     */
    public Map<String, Object> getAndRemoveTicketValue(String qrCodeTicket) {
        String value = redisTemplate.boundValueOps(qrcodePrefix + qrCodeTicket).get();
        redisTemplate.delete(qrcodePrefix + qrCodeTicket);
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

    /**
     * app扫码调用的接口
     *
     * @param ticket      qrcodeTicket
     * @param confirm     是否确认登录
     * @param accountName 扫码的用户名(手机号)
     * @param entCode     选择的账套编号
     * @param accountPic  未确认登录的时候，传入的用户头像，用来显示
     */
    public void scanLogin(String ticket,
                          boolean confirm,
                          String accountName,
                          String entCode,
                          String accountPic) {
        if (ticket.contains("=")) {
            ticket = ticket.substring(ticket.indexOf("=") + 1);
        }
        if (StringUtils.isEmpty(ticket) || !ticket.startsWith("sso")) {
            throw new RuntimeException("无效的二维码");
        }
        String value = redisTemplate.boundValueOps(qrcodePrefix + ticket).get();
        if (value == null || "".equals(value)) {//无效的二维码
            throw new RuntimeException("二维码已过期");
        } else {
            if (!value.equals("unknown")) {
                Map map = wrapExceptions(() -> objectMapper.readValue(value, Map.class), "json反序列化解析ticket值出错");
                String name = (String) map.get("accountName");
                if (name != null && !name.equals(accountName)) {
                    throw new RuntimeException("二维码已经被他人使用");
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("accountName", accountName);
            if (accountPic != null) {
                map.put("accountPic", accountPic);
            }
            if (entCode != null) {
                map.put("entCode", entCode);
            }
            map.put("confirm", confirm);
            redisTemplate.boundValueOps(qrcodePrefix + ticket).set(
                    ExceptionUtils.wrapExceptions(() -> objectMapper.writeValueAsString(map)),
                    TIMEOUT,
                    TimeUnit.MINUTES);
        }
    }

}