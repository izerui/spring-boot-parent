package thread.pool;
/**
 * @description disruptor代码样例。每10ms向disruptor中插入一个元素，消费者读取数据，并打印到终端
 */

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yj2025.disruptor.RunnableWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

// https://www.cnblogs.com/pku-liuqiang/p/8544700.html
@Slf4j
public class DisruptorTests {
    public static void main(String[] args) throws Exception {

        EventFactory<RunnableWrapper> eventFactory = new EventFactory<RunnableWrapper>() {
            @Override
            public RunnableWrapper newInstance() {
                return new RunnableWrapper();
            }
        };

        // 生产者的线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        // 处理Event的handler
        EventHandler<RunnableWrapper> handler = new EventHandler<RunnableWrapper>() {
            @Override
            public void onEvent(RunnableWrapper event, long sequence, boolean endOfBatch) throws Exception {
                log.info("onEvent: {}", event.toString());
                event.getRunnable().run();
            }
        };


        // 创建disruptor，采用单生产者模式
        Disruptor<RunnableWrapper> disruptor = new Disruptor(
                // RingBuffer生产工厂,初始化RingBuffer的时候使用
                eventFactory,
                // 指定RingBuffer的大小
                16,
                threadFactory,
                ProducerType.SINGLE,
                // 阻塞策略
                new BlockingWaitStrategy());

        // 设置EventHandler
        disruptor.handleEventsWith(handler);

        // 启动disruptor的线程
        RingBuffer<RunnableWrapper> ringBuffer = disruptor.start();

        long l = System.currentTimeMillis();
        AtomicInteger atomicLong = new AtomicInteger(1);
        while ((l + 10000L) > System.currentTimeMillis()) { // 10秒
            int andIncrement = atomicLong.getAndIncrement();
            ringBuffer.publishEvent((wrapper, sequence) -> {
                log.info("1: {}", andIncrement);
                wrapper.setRunnable(() -> {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("2: {}", andIncrement);
                });
            });
        }
        log.info("生产完毕------");
        disruptor.shutdown();
    }

}