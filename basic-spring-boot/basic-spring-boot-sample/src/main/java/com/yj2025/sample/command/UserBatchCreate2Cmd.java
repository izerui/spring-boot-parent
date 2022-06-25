package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.Producer;
import com.yj2025.sample.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserBatchCreate2Cmd extends BasicCommand<Void> {

    private int[] integers;

    public UserBatchCreate2Cmd(final int[] integers) {
        this.integers = integers;
    }

    @Override
    protected Void doExecute() throws Exception {
        EntityManager entityManager = Context.getBean(EntityManager.class);
        Producer<User> producer = Context.batchConsumer(User.class, 2, 500, new BatchConsumer<User>() {
            @Override
            protected void handlerEvent(List<User> correlationData, long sequence) throws Exception {
                logger.info("批次执行数量： {}", correlationData.size());
                Context.executeTransaction(status -> {
                    for (User user : correlationData) {
                        entityManager.persist(user);
                    }
                    entityManager.flush();
                    entityManager.clear();
                });
            }
        });

        for (Integer integer : integers) {
            producer.sendData(user -> {
                user.setCode("code" + integer);
                user.setName("张三丰");
                user.setEmail("张三丰@qq.com");
            });
            Thread.sleep(100);
        }
        log.info("发送个数：{}", integers.length);
        producer.shutdown();
        return null;
    }
}
