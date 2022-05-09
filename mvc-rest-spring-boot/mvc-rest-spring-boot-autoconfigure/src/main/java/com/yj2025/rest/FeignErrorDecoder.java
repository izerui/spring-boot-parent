package com.yj2025.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by serv on 2016/10/18.
 */
public class FeignErrorDecoder implements ErrorDecoder, Constants {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeignErrorDecoder.class);

    private ObjectMapper objectMapper;

    public FeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        Map map;
        try {
            String body = Util.toString(response.body().asReader());
            map = objectMapper.readValue(body, Map.class);
        } catch (IOException e) {
            return new IllegalArgumentException("feign response io error!" + e.getMessage());
        }
        try {
            String serializable = (String) map.get(EXCEPTION_SERIALIZABLE);

            //兼容老版本
            if (serializable == null) {
                serializable = (String) map.get("serializable");
            }
            Exception deserialize = (Exception) SerializationUtils.deserialize(Base64Utils.decodeFromString(serializable));
            return deserialize;
        } catch (Exception e) {
            return new RuntimeException((String) map.get("message"));
        } finally {
            if (map != null && map.get(EXCEPTION_APPLICATION_NAME) != null) {
                LOGGER.error("远程服务: {} 返回出错: {} 错误信息: {}", map.get(EXCEPTION_APPLICATION_NAME), methodKey, map.get("errMsg"));
            } else {
                LOGGER.error("返回出错: {} 错误信息: {}", methodKey, map.get("errMsg"));
            }
        }
    }
}
