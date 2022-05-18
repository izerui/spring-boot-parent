package com.yj2025.websocket.server.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.server.OnAfterForwardMessageHandler;
import com.yj2025.websocket.server.OnBeforeForwardMessageHandler;
import com.yj2025.websocket.server.UserNameLoader;
import com.yj2025.websocket.server.WebSocketServerProperties;
import com.yj2025.websocket.server.support.ChannelIdRedisTemplate;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by serv on 2015/4/28.
 */
public class UserChannelService {

    private static final Logger logger = LoggerFactory.getLogger(UserChannelService.class);

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private ChannelIdRedisTemplate redisTemplate;

    private ObjectMapper objectMapper;

    private WebSocketServerProperties serverProperties;

    private ObjectProvider<UserNameLoader> userNameLoaderObjectProvider;

    private ObjectProvider<OnBeforeForwardMessageHandler> beforeForwardMessageHandlers;
    private ObjectProvider<OnAfterForwardMessageHandler> afterForwardMessageHandlers;

    public UserChannelService(ChannelIdRedisTemplate redisTemplate,
                              ObjectMapper objectMapper,
                              WebSocketServerProperties serverProperties,
                              ObjectProvider<UserNameLoader> userNameLoaderObjectProvider,
                              ObjectProvider<OnBeforeForwardMessageHandler> beforeForwardMessageHandlers,
                              ObjectProvider<OnAfterForwardMessageHandler> afterForwardMessageHandlers) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.serverProperties = serverProperties;
        this.userNameLoaderObjectProvider = userNameLoaderObjectProvider;
        this.beforeForwardMessageHandlers = beforeForwardMessageHandlers;
        this.afterForwardMessageHandlers = afterForwardMessageHandlers;
    }

    /**
     * 监听消息并发送给js
     */
    public void sendToConsumer(WebMsg msg) throws Exception {
        beforeForwardMessageHandlers.forEach(onBeforeForwardMessageHandler -> {
            onBeforeForwardMessageHandler.handler(msg);
        });
        List<Channel> channels = findChannels(msg.getEntCode(), msg.getUserCode(), "*");
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(msg.getMessage())));
            activeConnect(channel);
        }
        afterForwardMessageHandlers.forEach(onAfterForwardMessageHandler -> {
            onAfterForwardMessageHandler.handler(msg);
        });
    }


    /**
     * 连接
     */
    public void connect(Channel channel, String message) throws Exception {
        logger.info("connection [{}]:{}", Thread.currentThread().getName(), message);
        //已连接忽略
        if (channel.hasAttr(AttributeKey.valueOf("key"))) {
            return;
        }
        Map<String, String> webMessage = objectMapper.readValue(message, Map.class);
        String entCode = webMessage.get("entCode");
        String userCode = webMessage.get("userCode");
        String userName = webMessage.get("userName");
        Assert.notNull(entCode);
        Assert.notNull(userCode);

        String key = getRedisKey(entCode, userCode, String.valueOf(Math.random()));
        channel.attr(AttributeKey.valueOf("key")).set(key);
        channel.attr(AttributeKey.valueOf("entCode")).set(entCode);
        channel.attr(AttributeKey.valueOf("userCode")).set(userCode);
        channel.attr(AttributeKey.valueOf("userName")).set(userName);
        channelGroup.add(channel);
        redisTemplate.boundValueOps(key).set(channel.id(), serverProperties.getChannelIdTimeoutMinutes(), TimeUnit.MINUTES);

        Map<String, String> result = new HashMap<>();
        result.put("type", "connected");
        channel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(result)));

    }

    /**
     * 断开连接
     */
    public void disconnect(Channel channel) {
        String key = (String) channel.attr(AttributeKey.valueOf("key")).get();
        String entCode = (String) channel.attr(AttributeKey.valueOf("entCode")).get();
        String userCode = (String) channel.attr(AttributeKey.valueOf("userCode")).get();
        if (key != null) {
            redisTemplate.delete(key);
        }
        channel.close();
        channelGroup.remove(channel);
    }


    /**
     * 激活通道
     *
     * @param channel
     */
    public void activeConnect(Channel channel) {
        String key = (String) channel.attr(AttributeKey.valueOf("key")).get();
        if (key != null && !"".equals(key)) {
            redisTemplate.boundValueOps(key).expire(serverProperties.getChannelIdTimeoutMinutes(), TimeUnit.MINUTES);
        }
    }

    /**
     * 在线连接的客户端ip地址列表
     *
     * @return
     */
    public int onlines() {
        return channelGroup.size();
    }

    /**
     * 获取账套下登陆的用户编号集合
     *
     * @param entCode
     * @return
     */
    public Set<String> onlineUsers(String entCode) {
        List<Channel> channels = findChannels(entCode, "*", "");
        Set<String> users = new LinkedHashSet<>();
        for (Channel channel : channels) {
            String userCode = (String) channel.attr(AttributeKey.valueOf("userCode")).get();
            AtomicReference<String> userName = new AtomicReference<>((String) channel.attr(AttributeKey.valueOf("userName")).get());
            if (StringUtils.isEmpty(userName.get())) {
                userNameLoaderObjectProvider.ifAvailable(userNameLoader -> {
                    userName.set(userNameLoader.getUserName(userCode));
                });
            }
            users.add(userName.get());
        }
        return users;
    }

    /**
     * 获取账套下登陆的用户编号集合
     *
     * @param entCode
     * @return
     */
    public Set<String> onlineUserMap(String entCode) {
        List<Channel> channels = findChannels(entCode, "*", "");
        Set<String> users = new LinkedHashSet<>();
        for (Channel channel : channels) {
            String userCode = (String) channel.attr(AttributeKey.valueOf("userCode")).get();
            if (StringUtils.isNotBlank(userCode)) {
                users.add(userCode);
            }
        }
        return users;
    }

    private List<Channel> findChannels(String entCode, String userCode, String random) {
        String keyParten = getRedisKey(entCode, userCode, random);
        Set<String> keys = redisTemplate.keys(keyParten);
        List<Channel> channels = new ArrayList<>();
        for (String key : keys) {
            ChannelId channelId = redisTemplate.boundValueOps(key).get();
            if (channelId != null) {
                Channel channel = channelGroup.find(channelId);
                if (channel != null) {
                    channels.add(channel);
                }
            }
        }
        return channels;
    }

    private String getRedisKey(String entCode, String userCode, String random) {
        String _entParten = StringUtils.isEmpty(entCode) ? "" : entCode + "-";
        String _userParten = StringUtils.isEmpty(userCode) ? "" : userCode;
        String _keyParten = serverProperties.getUserIdPrefix() + _entParten + _userParten;
        if (random != null && !random.equals("")) {
            _keyParten += random.equals("*") ? "*" : "-" + random;
        }
        return _keyParten;
    }

}
