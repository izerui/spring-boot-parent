package com.yj2025.performance;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 单线程批量消费者(如果支持批量尽量使用当前消费者模式)
 * 参考： https://www.icode9.com/content-4-1313516.html
 *
 * @author liuyuhua
 * @date 2022/5/24
 */
@Slf4j
public abstract class BatchConsumer<T> implements EventHandler<T> {

    /**
     * 每批次最多处理的数量
     */
    private final long batchLimitSize;

    private final static int RING_BATCH_SIZE = 1024 * 1024;

    /**
     * 积累的批次数据
     */
    private final List<T> correlationData = new ArrayList<>();

    public BatchConsumer(long batchLimitSize) {
        if (batchLimitSize <= 0) {
            throw new DisruptorException("请设置大于0的每批次消费数量限制");
        }
        this.batchLimitSize = batchLimitSize;
    }

    /**
     * 批量处理当前积累的批次数据
     *
     * @param correlationData 积累的数据
     * @param sequence        最后处理的序列
     * @throws Exception
     */
    protected abstract void handlerEvent(List<T> correlationData, long sequence) throws Exception;

    @Override
    public final void onEvent(T event, final long sequence, boolean endOfBatch) {
        try {
            handlerBatchEvents(event, sequence, endOfBatch);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 释放对象, 注意可能来不及gc，对象可能重用，注意重置相关值
        event = null;
    }

    /**
     * 积累待处理，并批量处理
     *
     * @param event      当前要处理的对象
     * @param sequence   当前消费到的队列位置
     * @param endOfBatch 是否为RingBuffer内存片中的最后一块
     */
    private void handlerBatchEvents(T event, long sequence, boolean endOfBatch) throws Exception {
        // 添加到批次
        correlationData.add(event);
        if ((sequence + 1) % batchLimitSize == 0) {
            handlerEvent(correlationData, sequence);
            // 重用数组
            correlationData.clear();
        }
        if (endOfBatch) {
            if ((sequence + 1) % RING_BATCH_SIZE != 0) {
                handlerEvent(correlationData, sequence);
                // 重用数组
                correlationData.clear();
            }
        }
    }


}
