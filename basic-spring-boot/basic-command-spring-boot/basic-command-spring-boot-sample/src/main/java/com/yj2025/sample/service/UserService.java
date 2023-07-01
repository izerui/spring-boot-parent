package com.yj2025.sample.service;

import com.google.common.base.Stopwatch;
import com.yj2025.basic.service.BasicService;
import com.yj2025.basic.web.support.AuthAware;
import com.yj2025.sample.command.*;
import com.yj2025.sample.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@Slf4j
public class UserService extends BasicService implements AuthAware {

    @Transactional
    public void add() {
        String userName = getUserName();
        log.info(userName);
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
//        execute(new UserDeleteCmd("sss"));
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setCode("code" + i);
//            user.setName("张2丰");
            user.setEmail("张三丰@qq.com");
            executeWhen(i > 5, new UserCreateCmd(user));
        }
    }

    @Transactional
    public void batchAdd() {
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd("使用 userRepository.saveAll 批量添加， 效率慢"));
        List<User> users = executeReturn(new UserBatchCreateCmd(IntStream.range(0, 20000).toArray()));
        for (User user : users) {
            log.info("{}", user.getId());
        }
    }

    @Transactional
    public void batchAdd2() {
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd("使用 Context.batchConsumer 批处理添加, 分批执行DbContext.batchUpdate 效率快"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        execute(new UserBatchCreate2Cmd(IntStream.range(0, 20000).toArray()));
        log.info("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd3() {
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd("使用 Context.multiConsumer 多消费者处理, 多线程消费，单个插入 SimpleJdbcInsert 效率慢"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        execute(new UserBatchCreate3Cmd(IntStream.range(0, 20000).toArray()));
        log.info("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd4() {
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd("使用 entityManager.persist 每批500 提交事务， 执行效率一般"));
        List<User> users = executeReturn(new UserBatchCreate4Cmd(IntStream.range(0, 20000).toArray()));
        for (User user : users) {
            log.info("{}", user.getId());
        }
    }

    @Transactional
    public void batchAdd5() {
        log.info("{}", TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd("使用 DbContext.batchUpdate 固定每批1000天假 ， 效率快，但是没 批量消费 batchAdd2 快"));
        execute(new UserBatchCreate5Cmd(IntStream.range(0, 20000).toArray()));
    }
}
