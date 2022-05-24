package com.yj2025.performance;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
@Slf4j
public class ProducerTest {


    @Test
    public void test01() throws InterruptedException {
        Consumer[] consumers = new Consumer<MyTask>() {
            @Override
            protected void handlerEvent(MyTask event) throws Exception {
                log.info("{}", event.getValue());
                Thread.sleep(10);
            }
        }.cloneSelfToMulti(6);

        BatchConsumer<MyTask> batchConsumer = new BatchConsumer<MyTask>(100) {
            @Override
            protected void handlerEvent(List<MyTask> accumulationDatas) throws Exception {
                log.info("当前处理 {} 条记录", accumulationDatas.size());
                Thread.sleep(10);
            }
        };

        Producer<MyTask> producer = Producer.builder()
                .requiredRingBufferSize(1024)
                .requiredDataType(MyTask.class)
//                .requiredConsumers(consumers)
                .requiredConsumers(batchConsumer)
                .build();

        AtomicInteger atomicInteger = new AtomicInteger(1);
        long l = System.currentTimeMillis();
        while ((l + 3000L) > System.currentTimeMillis()) { // 10秒
            producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
        }
        log.info("执行完毕, 消费者还在继续执行...");
        producer.shutdown();
        Thread.sleep(5000);
        log.info("消费完成,关闭处理器,总生产: {}", atomicInteger.get());
        log.info("再发一条测试关闭后还能不能发");
//        producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
//        System.exit(1);
    }

}
