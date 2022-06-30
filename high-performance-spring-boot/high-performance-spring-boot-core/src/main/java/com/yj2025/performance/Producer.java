package com.yj2025.performance;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

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
public final class Producer<T extends ClearEvent> implements DisposableBean {

    private final Builder builder;
    private transient Disruptor<T> disruptor;

    Producer(final Builder builder) {
        this.builder = builder;
        initDisruptor();
    }

    private void initDisruptor() {
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
            log.info("====== {} 并行处理器启动成功 ======", builder.consumers[0].getClass().getName());
            disruptor.handleEventsWithWorkerPool(builder.consumers);
        }
        // 设置EventHandler 被一个批量处理消费者消费
        // https://www.jianshu.com/p/f4021e8141ad
        if (builder.batchConsumer != null) {
            log.info("====== {} 批处理器启动成功 ======", builder.batchConsumer.getClass().getName());
            disruptor.handleEventsWith(builder.batchConsumer);
        }
        disruptor.start();
    }


    /**
     * 发送补全的数据到待处理缓冲区, 注意: 无论如何补全后的数据都会发送到缓冲区，所以需要选择性处理的话，请自行标记，并在消费者端过滤掉。
     *
     * @param consumer
     */
    public void sendData(java.util.function.Consumer<T> consumer) {
        if (disruptor == null) {
            log.warn("线程池未初始化,重新初始化...");
            initDisruptor();
        }
        RingBuffer<T> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            T obj = ringBuffer.get(sequence);
            // 重用对象之前重置相关值
            obj.clear();
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
        this.disruptor.shutdown();
        this.disruptor = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    public static class Builder {
        /**
         * 指定RingBuffer的大小
         */
        private int ringBufferSize = 1024 * 64;
        private Class dataType;
        private ProducerType producerType = ProducerType.MULTI;
        private ThreadFactory threadFactory = new Consumer.ConsumerThreadFactory();
        private WaitStrategy waitStrategy = new BlockingWaitStrategy();
        private Consumer[] consumers;
        private BatchConsumer batchConsumer;
        private long batchLimitSize;
        private int maxWaitSeconds;

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
         * 等待策略，批量模式下无效
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
         * @param maxWaitSeconds 当消费过快的时候，未达到批次最大等待秒数
         * @param batchLimitSize
         * @param batchConsumer
         * @param <T>
         * @return
         */
        public <T extends ClearEvent> Builder requiredConsumers(int maxWaitSeconds, long batchLimitSize, BatchConsumer<T> batchConsumer) {
            if (batchLimitSize <= 0) {
                throw new DisruptorException("请设置大于0的每批次消费数量限制");
            }
            this.maxWaitSeconds = maxWaitSeconds;
            this.batchLimitSize = batchLimitSize;
            this.batchConsumer = batchConsumer;
            return this;
        }

        /**
         * 并行工作消费者
         *
         * @param consumers
         * @return
         */
        public <T extends ClearEvent> Builder requiredConsumers(Consumer<T>... consumers) {
            this.consumers = consumers;
            return this;
        }

        /**
         * 并行工作消费者
         *
         * @param consumers
         * @return
         */
        public <T extends ClearEvent> Builder requiredConsumers(Collection<Consumer<T>> consumers) {
            this.consumers = consumers.toArray(new Consumer[consumers.size()]);
            return this;
        }

        /**
         * 开始创建生产者
         *
         * @return
         */
        public Producer build() {
            if (dataType == null) {
                throw new DisruptorException("dataType数据类型不能为空!");
            }
            if (consumers == null && batchConsumer == null) {
                throw new DisruptorException("请至少设置一种消费者处理器!");
            } else if (consumers != null && batchConsumer != null) {
                throw new DisruptorException("最多只能设置一种消费处理器,要么批量消费,要么分别消费!");
            }
            if (batchConsumer != null) {
                if (batchLimitSize >= ringBufferSize) {
                    throw new RuntimeException("批次数量必须小于环形缓冲区数值");
                }
                this.waitStrategy = new BatchWaitStrategy(maxWaitSeconds, batchLimitSize);
            }
            return new Producer(
                    this
            );
        }

    }

}
