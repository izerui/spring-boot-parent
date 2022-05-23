package com.yj2025.disruptor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
@Slf4j
public class ProducerTest {


    @Test
    public void test01() {
        Consumer[] consumers = new Consumer<MyTask>() {
            @Override
            public void onEvent(MyTask event) throws Exception {
                log.info("{}", event.getValue());
                Thread.sleep(10);
            }
        }.cloneSelfToMulti(6);
        Producer<MyTask> producer = Producer.builder()
                .dataType(MyTask.class)
                .ringBufferSize(1024)
                .consumers(consumers)
                .build();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        long l = System.currentTimeMillis();
        while ((l + 10000L) > System.currentTimeMillis()) { // 10秒
            producer.sendData(o -> o.setValue(atomicInteger.getAndIncrement()));
        }
        log.info("执行完毕------总生产: {}", atomicInteger.get());
        System.exit(1);
    }

}
