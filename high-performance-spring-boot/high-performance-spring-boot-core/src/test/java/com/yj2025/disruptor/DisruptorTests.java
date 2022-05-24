package com.yj2025.disruptor;
/**
 * @description disruptor代码样例。每10ms向disruptor中插入一个元素，消费者读取数据，并打印到终端
 */

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// https://www.cnblogs.com/pku-liuqiang/p/8544700.html
@Slf4j
public class DisruptorTests {
    public static void main(String[] args) throws Exception {
        EventFactory<MyTask> eventFactory = () -> new MyTask();

        // 处理Event的handler
        WorkHandler<MyTask>[] handlers = new WorkHandler[6];
        for (int i = 0; i < 6; i++) {
            handlers[i] = event -> {
                log.info("handler: {}", event.getValue());
                Thread.sleep(10);
            };
        }


        // 创建disruptor，采用单生产者模式
        Disruptor<MyTask> disruptor = new Disruptor(
                // RingBuffer生产工厂,初始化RingBuffer的时候使用
                eventFactory,
                // 指定RingBuffer的大小
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                // 阻塞策略
                new SleepingWaitStrategy());

        // 设置EventHandler
        disruptor.handleEventsWithWorkerPool(handlers);

        // 启动disruptor的线程
        RingBuffer<MyTask> ringBuffer = disruptor.start();

        long l = System.currentTimeMillis();
        AtomicInteger atomicLong = new AtomicInteger(1);
        while ((l + 10000L) > System.currentTimeMillis()) { // 10秒
            ringBuffer.publishEvent((event, sequence) -> {
                event.setValue(atomicLong.getAndIncrement());
            });
        }
        log.info("执行完毕------总生产: {}", atomicLong.get());
        System.exit(1);
    }

}
