package com.yj2025.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * 线程安全的生产者
 *
 * @author liuyuhua
 * @date 2022/5/23
 */
public class Producer<T> {

    private final Disruptor<T> disruptor;
    /**
     * 处理器关闭前记录的游标值，再发送数据则判断异常。
     */
    private transient Long shutdownCursor;

    Producer(final Builder builder) {
        EventFactory eventFactory = () -> {
            try {
                return builder.dataType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
        // 创建disruptor，采用单生产者模式
        disruptor = new Disruptor(
                // RingBuffer生产工厂,初始化RingBuffer的时候使用
                eventFactory,
                // 指定RingBuffer的大小
                builder.ringBufferSize,
                builder.threadFactory,
                builder.producerType,
                builder.waitStrategy);
        // https://lmax-exchange.github.io/disruptor/user-guide/index.html#_batch_rewind
        // 设置EventHandler
        EventHandlerGroup<T> tEventHandlerGroup = disruptor.handleEventsWithWorkerPool(builder.consumers);
//                .and(new BatchEventProcessor<T>(null, null, null))
        disruptor.start();
    }

    /**
     * 发送补全的数据到待处理缓冲区
     *
     * @param consumer
     */
    public void sendData(java.util.function.Consumer<T> consumer) {
        RingBuffer<T> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        if (this.shutdownCursor != null && sequence > shutdownCursor) {
            throw new DisruptorException("生产者已经关闭,不再接收新的数据产生!");
        }
        try {
            T obj = ringBuffer.get(sequence);
            consumer.accept(obj);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 等待所有发送的数据执行完后，停止处理器。<br/>
     * 调用此方法后，请不要再发送数据，否则可能永远无法停止。
     */
    public void shutdown() {
        // 关闭前记录当前游标
        this.shutdownCursor = disruptor.getCursor();
        this.disruptor.shutdown();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * 指定RingBuffer的大小
         */
        private int ringBufferSize = 1024 * 1024;
        private Class dataType;
        private ProducerType producerType = ProducerType.MULTI;
        private ThreadFactory threadFactory = new Consumer.ConsumerThreadFactory();
        private WaitStrategy waitStrategy = new YieldingWaitStrategy();
        private Consumer[] consumers;
        private int autoCloseOnCompleteSize;

        /**
         * 环形缓冲区大小
         *
         * @param ringBufferSize
         * @return
         */
        public Builder requiredRingBufferSize(int ringBufferSize) {
            this.ringBufferSize = ringBufferSize;
            return this;
        }

        /**
         * 用来预初始化data类型的
         *
         * @param dataType
         * @return
         */
        public Builder requiredDataType(Class dataType) {
            this.dataType = dataType;
            return this;
        }

        /**
         * 单线程生产者类型(性能最高) 多线程生产者类型
         *
         * @param producerType
         * @return
         */
        public Builder optionnalProducerType(ProducerType producerType) {
            this.producerType = producerType;
            return this;
        }

        /**
         * 线程工厂
         *
         * @param threadFactory
         * @return
         */
        public Builder optionnalThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        /**
         * 拒绝策略
         *
         * @param waitStrategy
         * @return
         */
        public Builder optionnalWaitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        /**
         * 并行工作消费者
         *
         * @param consumers
         * @return
         */
        public <T> Builder requiredConsumers(Consumer<T>... consumers) {
            this.consumers = consumers;
            return this;
        }

        /**
         * 并行工作消费者
         *
         * @param consumers
         * @return
         */
        public <T> Builder requiredConsumers(Collection<Consumer<T>> consumers) {
            this.consumers = consumers.toArray(new Consumer[consumers.size()]);
            return this;
        }

        /**
         * 开始创建生产者
         *
         * @return
         */
        public Producer build() {
            if (consumers == null) {
                throw new DisruptorException("请添加consumers处理器");
            }
            if (dataType == null) {
                throw new DisruptorException("dataType数据类型不能为空");
            }
            return new Producer(
                    this
            );
        }

    }

}
