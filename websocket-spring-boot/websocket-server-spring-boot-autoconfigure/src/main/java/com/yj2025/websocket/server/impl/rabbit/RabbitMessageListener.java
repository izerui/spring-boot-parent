package com.yj2025.websocket.server.impl.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.server.impl.UserChannelService;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

public class RabbitMessageListener {

    private ObjectMapper objectMapper;
    private UserChannelService userChannelService;

    public RabbitMessageListener(ObjectMapper objectMapper, UserChannelService userChannelService) {
        this.objectMapper = objectMapper;
        this.userChannelService = userChannelService;
    }

    /**
     * 监听消息并发送给js
     */
    @Transactional
    @RabbitListener(
            bindings = @QueueBinding(
                    /**
                     * name: 队列的名称；
                     * durable: 是否持久化；
                     * exclusive: 是否独享、排外的；
                     * autoDelete: 是否自动删除；
                     * arguments：队列的其他属性参数，有如下可选项，可参看图2的arguments：
                    */
                    value = @Queue(
                            value = "#{queueName.getQueueName()}",
                            durable = "false",
                            autoDelete = "true",
                            /**
                             * x-message-ttl：消息的过期时间，单位：毫秒；
                             * x-expires：队列过期时间，队列在多长时间未被访问将被删除，单位：毫秒；
                             * x-max-length：队列最大长度，超过该最大值，则将从队列头部开始删除消息；
                             * x-max-length-bytes：队列消息内容占用最大空间，受限于内存大小，超过该阈值则从队列头部开始删除消息；
                             * x-overflow：设置队列溢出行为。这决定了当达到队列的最大长度时消息会发生什么。有效值是drop-head、reject-publish或reject-publish-dlx。仲裁队列类型仅支持drop-head；
                             * x-dead-letter-exchange：死信交换器名称，过期或被删除（因队列长度超长或因空间超出阈值）的消息可指定发送到该交换器中；
                             * x-dead-letter-routing-key：死信消息路由键，在消息发送到死信交换器时会使用该路由键，如果不设置，则使用消息的原来的路由键值
                             * x-single-active-consumer：表示队列是否是单一活动消费者，true时，注册的消费组内只有一个消费者消费消息，其他被忽略，false时消息循环分发给所有消费者(默认false)
                             * x-max-priority：队列要支持的最大优先级数;如果未设置，队列将不支持消息优先级；
                             * x-queue-mode（Lazy mode）：将队列设置为延迟模式，在磁盘上保留尽可能多的消息，以减少RAM的使用;如果未设置，队列将保留内存缓存以尽可能快地传递消息；
                             * x-queue-master-locator：在集群模式下设置镜像队列的主节点信息。
                            */
                            arguments = {
                                    @Argument(name = "x-expires", value = "10000", type = "java.lang.Long"),
                                    @Argument(name = "x-max-length", value = "100000", type = "java.lang.Long")
                            }),
                    key = {"#{webSocketServerProperties.rabbit.routingKey}"},
                    exchange = @Exchange(
                            value = "#{webSocketServerProperties.rabbit.exchange}",
                            type = "topic"))
    )
    public void process(Message<byte[]> message) throws Exception {
        WebMsg webMsg = objectMapper.readValue(message.getPayload(), WebMsg.class);
        userChannelService.sendToConsumer(webMsg);
    }

}
