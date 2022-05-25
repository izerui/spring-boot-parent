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
     * 每批次最多处理的数量不能大于2000
     */
    private final long batchLimitSize;

    /**
     * 最大等待毫秒时间
     */
    private final static int maxWaitTimeMillis = 2000;

    /**
     * 积累的批次数据
     */
    private final List<T> correlationData = new ArrayList<>();

    public BatchConsumer(long batchLimitSize) {
        if (batchLimitSize <= 0) {
            throw new DisruptorException("请设置大于0的每批次消费数量限制");
        }
        if (batchLimitSize > maxWaitTimeMillis) {
            throw new DisruptorException("批处理数量不能大于" + maxWaitTimeMillis + "个");
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
        // 释放对象
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
        int batchNum = correlationData.size();
        if (batchNum >= batchLimitSize) {
            handlerEvent(correlationData, sequence);
            // 重用数组
            correlationData.clear();
            log.debug("队列已满,快速开始下一个...");
            return;
        }
        // 当前指示最后序列, 后续未收到数据,则执行后,等待一段时间
        if (endOfBatch) {
            handlerEvent(correlationData, sequence);
            // 重用数组
            correlationData.clear();
            // 等待当前批次缺少的数量(1个按1毫秒等待)
            long waitTimeMillis = batchLimitSize - batchNum;
            // 如果等待时间超出预订最大时间，则按预订最大等待时间
            if (waitTimeMillis > maxWaitTimeMillis) {
                waitTimeMillis = maxWaitTimeMillis;
            }
            log.debug("缺 " + waitTimeMillis + "个, 等待 " + waitTimeMillis + "毫秒,争取下个批次打满");
            Thread.sleep(Math.abs(waitTimeMillis));
        }
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 1029; i++) {
//            int next = (i + 1) % RING_BATCH_SIZE;
//            System.out.println(i + " - " + next);
//            if (next != 0) {
//                System.out.println("等待 " + (RING_BATCH_SIZE - next) + "毫秒");
//            }
//        }
//    }


}
