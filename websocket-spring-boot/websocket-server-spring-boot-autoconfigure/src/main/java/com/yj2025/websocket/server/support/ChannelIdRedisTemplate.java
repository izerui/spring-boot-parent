package com.yj2025.websocket.server.support;

import io.netty.channel.ChannelId;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by serv on 2015/4/20.
 */
public class ChannelIdRedisTemplate extends RedisTemplate<String, ChannelId> {

    public ChannelIdRedisTemplate() {
        RedisSerializer<String> string = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdk = new JdkSerializationRedisSerializer();
        setKeySerializer(string);
        setValueSerializer(jdk);
        setHashKeySerializer(string);
        setHashValueSerializer(jdk);
    }

    public ChannelIdRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }


}
