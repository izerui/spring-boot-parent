package com.yj2025.websocket.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yj2025.websocket.server.impl.UserChannelService;
import com.yj2025.websocket.server.impl.rabbit.RabbitConfiguration;
import com.yj2025.websocket.server.support.ChannelIdRedisTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@Import({RabbitConfiguration.class})
public class WebSocketServerconfiguration {

    @Bean
    public WebSocketServerProperties webSocketServerProperties() {
        return new WebSocketServerProperties();
    }

    @Bean
    @Primary
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return objectMapper;
    }

    @Bean
    public ChannelIdRedisTemplate channelIdBeanRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new ChannelIdRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public WebSocketServer server(WebSocketServerProperties properties, UserChannelService userService) {
        return new WebSocketServer(properties.getPort(), userService);
    }


    @Bean
    public UserChannelService userService(ChannelIdRedisTemplate redisTemplate,
                                          ObjectMapper objectMapper,
                                          ObjectProvider<UserNameLoader> userNameLoaderObjectProvider,
                                          ObjectProvider<OnBeforeForwardMessageHandler> beforeForwardMessageHandlers,
                                          ObjectProvider<OnAfterForwardMessageHandler> afterForwardMessageHandlers) {
        return new UserChannelService(
                redisTemplate,
                objectMapper,
                userNameLoaderObjectProvider,
                beforeForwardMessageHandlers,
                afterForwardMessageHandlers);
    }


}
