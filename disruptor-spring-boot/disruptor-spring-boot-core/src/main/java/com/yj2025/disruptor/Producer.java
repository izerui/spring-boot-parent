package com.yj2025.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;

import java.util.Collection;
import java.util.concurrent.ThreadFactory;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
public class Producer<T> {

    private final Disruptor<T> disruptor;

    Producer(final EventFactory<T> eventFactory,
             final int ringBufferSize,
             final ThreadFactory threadFactory,
             final ProducerType producerType,
             final WaitStrategy waitStrategy,
             final Consumer<T>... consumers) {
        // 创建disruptor，采用单生产者模式
        disruptor = new Disruptor(
                // RingBuffer生产工厂,初始化RingBuffer的时候使用
                eventFactory,
                // 指定RingBuffer的大小
                ringBufferSize,
                threadFactory,
                producerType,
                waitStrategy);
        // 设置EventHandler
        EventHandlerGroup<T> tEventHandlerGroup = disruptor.handleEventsWithWorkerPool(consumers)
                // https://lmax-exchange.github.io/disruptor/user-guide/index.html#_using_the_disruptor
//                .and(new BatchEventProcessor<T>(null, null, null))
                .then(new ClearEventHandler<>());
        disruptor.start();
    }

    public void sendData(java.util.function.Consumer<T> consumer) {
        RingBuffer<T> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            T obj = ringBuffer.get(sequence);
            consumer.accept(obj);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void shutdown() {
        disruptor.shutdown();
    }

    @Data
    public static class Builder {
        /**
         * 指定RingBuffer的大小
         */
        private int ringBufferSize = 1024 * 1024;
        public Class dataType;
        private ProducerType producerType = ProducerType.MULTI;
        private ThreadFactory threadFactory = new Consumer.ConsumerThreadFactory();
        private WaitStrategy waitStrategy = new YieldingWaitStrategy();
        private Consumer[] consumers;

        public Builder ringBufferSize(int ringBufferSize) {
            this.ringBufferSize = ringBufferSize;
            return this;
        }

        public Builder dataType(Class dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder producerType(ProducerType producerType) {
            this.producerType = producerType;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder waitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        public <T> Builder consumers(Consumer<T>... consumers) {
            this.consumers = consumers;
            return this;
        }

        public <T> Builder consumers(Collection<Consumer<T>> consumers) {
            this.consumers = consumers.toArray(new Consumer[consumers.size()]);
            return this;
        }

        public Producer build() {
            if (consumers == null) {
                throw new RuntimeException("请添加consumers处理器");
            }
            EventFactory eventFactory = () -> {
                try {
                    return dataType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            };
            return new Producer(
                    eventFactory,
                    ringBufferSize,
                    threadFactory,
                    producerType,
                    waitStrategy,
                    consumers
            );
        }


    }

}
