package com.yj2025.performance;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.concurrent.ThreadFactory;

/**
 * 数据生产者
 * https://lmax-exchange.github.io/disruptor/user-guide/index.html#_batch_rewind
 *
 * @author liuyuhua
 * @date 2022/5/23
 */
@Slf4j
public class Producer<T> implements DisposableBean, InitializingBean {

    private final Builder builder;
    private transient Disruptor<T> disruptor;

    public Producer(Class<T> dataType, Consumer<T>... consumers) {
        this(dataType, null, consumers);
    }

    public Producer(Class<T> dataType, BatchConsumer<T> batchConsumer) {
        this(dataType, batchConsumer, null);
    }

    private Producer(Class<T> dataType, BatchConsumer<T> batchConsumer, Consumer<T>[] consumers) {
        this.builder = new Builder();
        this.builder.dataType = dataType;
        this.builder.batchConsumer = batchConsumer;
        this.builder.consumers = consumers;
        Customizer<Builder> customizer = Customizer.withDefaults();
        customizer.customize(this.builder);
        customize(customizer);
    }

    protected void customize(Customizer<Builder> customizer) {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.builder.dataType == null) {
            throw new DisruptorException("dataType数据类型不能为空!");
        }
        if (this.builder.consumers == null && this.builder.batchConsumer == null) {
            throw new DisruptorException("请至少设置一种消费者处理器!");
        } else if (this.builder.consumers != null && this.builder.batchConsumer != null) {
            throw new DisruptorException("最多只能设置一种消费处理器,要么批量消费,要么分别消费!");
        }
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
        // 设置WorkHandler 同一事件会被一组消费者其中之一消费
        if (builder.consumers != null) {
            log.info("{} 并行处理器启动成功", this.builder.dataType.getName());
            disruptor.handleEventsWithWorkerPool(builder.consumers);
        }
        // 设置EventHandler 被一个批量处理消费者消费
        // https://www.jianshu.com/p/f4021e8141ad
        if (builder.batchConsumer != null) {
            log.info("{} 批处理器启动成功", this.builder.dataType.getName());
            disruptor.handleEventsWith(builder.batchConsumer);
        }
        disruptor.start();
    }

    /**
     * 发送补全的数据到待处理缓冲区
     *
     * @param consumer
     */
    public void sendData(ThrowsConsumer<T> consumer) throws Exception {
        if (disruptor == null) {
            log.warn("线程池未初始化,通过afterPropertiesSet初始化...");
            afterPropertiesSet();
        }
        RingBuffer<T> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            T obj = ringBuffer.get(sequence);
            consumer.accept(obj);
        } finally {
            ringBuffer.publish(sequence);
        }
    }


    /**
     * 等待所有发送的数据执行完后，停止处理器。<br/>
     */
    @Override
    public void destroy() throws Exception {
        this.disruptor.shutdown();
        this.disruptor = null;
    }

    public static class Builder {
        /**
         * 指定RingBuffer的大小
         */
        private int ringBufferSize = 1024 * 8;
        private Class dataType;
        private ProducerType producerType = ProducerType.MULTI;
        private ThreadFactory threadFactory = new Consumer.ConsumerThreadFactory();
        private WaitStrategy waitStrategy = new YieldingWaitStrategy();
        private Consumer[] consumers;
        private BatchConsumer batchConsumer;

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
         * 批量消费者(优先使用)
         *
         * @param batchConsumer
         * @return
         */
        public <T> Builder requiredConsumers(BatchConsumer<T> batchConsumer) {
            this.batchConsumer = batchConsumer;
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

    }

}
