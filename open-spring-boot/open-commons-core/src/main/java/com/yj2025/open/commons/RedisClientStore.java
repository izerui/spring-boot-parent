package com.yj2025.open.commons;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author liuyuhua
 */
public class RedisClientStore implements ClientStore {

    private final static String CLIENT_SECRET_PREFIX = "open:client:";

    private StringRedisTemplate redisTemplate;

    public RedisClientStore(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    public RedisClientStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveClientSecret(String clientId, String clientSecret) {
        redisTemplate.boundValueOps(CLIENT_SECRET_PREFIX + clientId).set(clientSecret, Constants.CLIENT_SECRET_VALIDITY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public String getClientSecret(String clientId) {
        return redisTemplate.boundValueOps(CLIENT_SECRET_PREFIX + clientId).get();
    }
}
