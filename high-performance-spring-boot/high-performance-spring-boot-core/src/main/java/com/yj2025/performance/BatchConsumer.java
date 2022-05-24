package com.yj2025.performance;

import com.lmax.disruptor.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 单线程批量消费者
 *
 * @author liuyuhua
 * @date 2022/5/24
 */
public abstract class BatchConsumer<T> implements EventHandler<T> {

    /**
     * 每批次最多处理的数量
     */
    private final long batchLimitSize;

    /**
     * 批处理等待批次增加时间2秒，到期无条件执行批处理
     */
    private final static long delayedWaitTimeMillis = 2000;
    /**
     * 最后一次批处理时间
     */
    private transient long lastHandlerTimeMillis = 0L;
//    private final transient Thread daemonThread;

    /**
     * 积累的批次数据
     */
    private final List<T> batchDatas = new ArrayList<>();
    private transient long batchDatasNum = 0;

    public BatchConsumer(long batchLimitSize) {
        if (batchLimitSize <= 0) {
            throw new DisruptorException("请设置大于0的每批次消费数量限制");
        }
        if (batchLimitSize % 2 != 0) {
            throw new DisruptorException("请设置批次消费数量为2的倍数");
        }
        this.batchLimitSize = batchLimitSize;
//        this.daemonThread = new Thread(() -> {
//            try {
//                while (true) {
//                    // 最后一次触发批量处理已经是2秒前了
//                    if (lastHandlerTimeMillis + 2000 <= System.currentTimeMillis()) {
//                        onEvent(null, 0, true);
//                    }
//                    Thread.sleep(delayedWaitTimeMillis);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
//        });
//        this.daemonThread.setDaemon(true);
//        this.daemonThread.setName("Daemon-" + Thread.currentThread().getName());
//        this.daemonThread.start();
    }

    /**
     * 批量处理当前积累的批次数据
     *
     * @param accumulationDatas 积累的数据
     * @throws Exception
     */
    protected abstract void handlerEvent(List<T> accumulationDatas) throws Exception;

    @Override
    public final void onEvent(T event, final long sequence, boolean endOfBatch) throws Exception {
        // 批次数量满了或者溢出则触发批处理
        if (!isBatchEventsOverflow()) {
            // 添加到批次
            addBatchEvents(event);
            if (lastHandlerTimeMillis + 2000 <= System.currentTimeMillis()) {
                Thread.sleep(500);
            }
        } else {
            handlerBatchEvents();
        }
        lastHandlerTimeMillis = System.currentTimeMillis();
        // 释放对象
        event = null;
    }

    private boolean isBatchEventsOverflow() {
        return batchDatasNum >= batchLimitSize;
    }

    /**
     * 添加到批次
     *
     * @param event
     */
    private void addBatchEvents(T event) {
        if (event != null) {
            batchDatas.add(event);
            batchDatasNum++;
        }
    }

    /**
     * 执行批次处理
     *
     * @throws Exception
     */
    private void handlerBatchEvents() throws Exception {
        if (batchDatasNum == 0) {
            return;
        }
        handlerEvent(batchDatas);
        // 重用数组
        batchDatas.clear();
        batchDatasNum = 0;
    }


}
