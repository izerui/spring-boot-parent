package com.yj2025.performance;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
@Slf4j
public class ProducerTest {

    @Test
    public void test01() throws Exception {
        Consumer[] consumers = new Consumer<MyTask>() {
            @Override
            protected void handlerEvent(MyTask event) throws Exception {
                log.info("{}", event.getValue() + 1);
                Thread.sleep(2);
            }
        }.cloneSelfToMulti(5);
        Producer<MyTask> producer = Producer.builder()
                .requiredDataType(MyTask.class)
                .requiredConsumers(consumers)
                .build();
        execute(producer);
    }

    @Test
    public void test02() throws Exception {

        BatchConsumer<MyTask> batchConsumer = new BatchConsumer<MyTask>(1000) {

            @Override
            protected void handlerEvent(List<MyTask> correlationData, long sequence) throws Exception {
//                len.getAndAdd(accumulationDatas.size());
                log.info("当前处理: {} 条", correlationData.size());
                Thread.sleep(10);
            }
        };
        Producer<MyTask> producer = Producer.builder()
                .requiredDataType(MyTask.class)
                .requiredConsumers(batchConsumer)
                .build();
        execute(producer);
    }

    private void execute(Producer<MyTask> producer) throws Exception {
        // 3个生产者，每个生产3秒过程
        AtomicInteger atomicInteger = new AtomicInteger(0);
        long l = System.currentTimeMillis();
        CompletableFuture[] futures = new CompletableFuture[3];
//        for (int i = 0; i < 3; i++) {
//            futures[i] = CompletableFuture.runAsync(() -> {
//                while ((l + 3000L) > System.currentTimeMillis()) { // 3秒
//                    try {
//                        producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//        for (CompletableFuture future : futures) {
//            future.get();
//        }

        while ((l + 100000L) > System.currentTimeMillis()) { // 3秒
            producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
//            Thread.sleep(RandomUtils.nextInt(10, 20));
        }
        log.info("执行完毕, 消费者还在继续执行...");
        producer.shutdown();
        log.info("消费完成,关闭处理器,总生产: {}", atomicInteger.get());
        log.info("再发一条测试关闭后还能不能发");
        producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
//        System.exit(1);
    }

}
