package com.yj2025.sample.command;

import com.yj2025.command.AbstractCommand;
import com.yj2025.command.Context;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.Producer;
import com.yj2025.sample.entity.User;

import javax.persistence.EntityManager;
import java.util.List;

public class UserBatchCreate2Cmd extends AbstractCommand<int[], Void> {

    public UserBatchCreate2Cmd(int[] integers) {
        super(integers);
    }

    @Override
    protected Void doExecute(int[] parameter) throws Exception {
        EntityManager entityManager = Context.getBean(EntityManager.class);
        Producer<User> producer = Context.batch(User.class, 4096, new BatchConsumer<User>(500) {
            @Override
            protected void handlerEvent(List<User> correlationData, long sequence) throws Exception {
                Context.executeManualTransaction(status -> {
                    for (User user : correlationData) {
                        entityManager.persist(user);
                    }
                    logger.info("批次执行数量： {}", correlationData.size());
                    entityManager.flush();
                    entityManager.clear();
                });
            }
        });

        for (Integer integer : parameter) {
            producer.sendData(user -> {
                user.setId(null);
                user.setCode("code" + integer);
                user.setName("张三丰");
                user.setEmail("张三丰@qq.com");
            });
        }
        producer.shutdown();
        return null;
    }
}
