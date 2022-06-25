package com.yj2025.performance;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * 单线程批量消费者(如果支持批量尽量使用当前消费者模式)
 * 参考： https://www.icode9.com/content-4-1313516.html
 *
 * @author liuyuhua
 * @date 2022/5/24
 */
@Slf4j
public abstract class BatchConsumer<T extends ClearEvent> extends TimerTask implements EventHandler<T> {

    private final TimerDaemonThread timer;
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
        this.timer = new TimerDaemonThread(this, 1000);
        this.timer.start();
    }

    /**
     * 批量处理当前积累的批次数据
     *
     * @param correlationData 积累的数据
     * @throws Exception
     */
    protected abstract void handlerEvent(List<T> correlationData) throws Exception;

    @Override
    public final void onEvent(T event, final long sequence, boolean endOfBatch) {
        try {
            handlerBatchEvents(event, sequence, endOfBatch);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 积累待处理，并批量处理
     *
     * @param event      当前要处理的对象
     * @param sequence   当前消费到的队列位置
     * @param endOfBatch 是否为RingBuffer内存片中的最后一块
     */
    private void handlerBatchEvents(T event, long sequence, boolean endOfBatch) throws Exception {
        timer.pause();
        synchronized (correlationData) {
            // 添加到批次
            correlationData.add(event);
        }
        // 够批次后批量处理，并继续消费
        if (correlationData.size() >= batchLimitSize) {
            executeBatch();
            return;
        } else {
            timer.next();
        }
    }

    @Override
    public void run() {
        try {
            executeBatch();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void executeBatch() throws Exception {
        synchronized (correlationData) {
            if (correlationData.size() > 0) {
                handlerEvent(correlationData);
                // 重用数组
                correlationData.clear();
            }
        }
    }

    public long getBatchLimitSize() {
        return batchLimitSize;
    }

    public boolean isFull() {
        return correlationData.size() >= batchLimitSize;
    }
}
