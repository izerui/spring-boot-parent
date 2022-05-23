package com.yj2025.disruptor;

import com.lmax.disruptor.WorkHandler;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
public abstract class Consumer<T> implements WorkHandler<T>, Cloneable {

    /**
     * 复制消费者变成多个
     * @param poolSize
     * @return
     */
    public Consumer[] cloneSelfToMulti(int poolSize) {
        try {
            Consumer[] consumers = new Consumer[poolSize];
            for (int i = 0; i < poolSize; i++) {
                consumers[i] = this.clone();
            }
            return consumers;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected Consumer clone() throws CloneNotSupportedException {
        return (Consumer) super.clone();
    }
}
