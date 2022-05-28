package com.yj2025.performance;

import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程消费者，每个消费者独占一个线程
 *
 * @author liuyuhua
 * @date 2022/5/23
 */
public abstract class Consumer<T> implements WorkHandler<T>, Cloneable {

    protected abstract void handlerEvent(T event) throws Exception;

    @Override
    public final void onEvent(T event) throws Exception {
        handlerEvent(event);
        // 释放对象
        event = null;
    }


    /**
     * 复制消费者变成多个
     *
     * @param multiNum
     * @return
     */
    public Consumer[] cloneSelfToMulti(int multiNum) {
        try {
            Consumer[] consumers = new Consumer[multiNum];
            for (int i = 0; i < multiNum; i++) {
                consumers[i] = this.clone();
            }
            return consumers;
        } catch (CloneNotSupportedException e) {
            throw new DisruptorException(e.getMessage(), e);
        }
    }

    @Override
    protected Consumer clone() throws CloneNotSupportedException {
        return (Consumer) super.clone();
    }

    /**
     * @author liuyuhua
     * @date 2022/5/23
     */
    public static class ConsumerThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public ConsumerThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "disruptor-" +
                    poolNumber.getAndIncrement() +
                    "-consumer-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
