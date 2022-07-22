package com.yj2025.sample.service;

import com.google.common.base.Stopwatch;
import com.yj2025.basic.command.CommandInvoker;
import com.yj2025.basic.service.BasicService;
import com.yj2025.basic.support.WebRequestAware;
import com.yj2025.sample.command.*;
import com.yj2025.sample.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class UserService extends BasicService implements WebRequestAware {

    @Transactional
    public void add() {
        String userName = getUserName();
        System.out.println(userName);
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd());
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张2丰");
            user.setEmail("张三丰@qq.com");
            executeWhen(i > 5, new UserCreateCmd(user));
        }
    }

    @Transactional
    public void batchAdd() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd());
        List<User> users = executeReturn(new UserBatchCreateCmd(IntStream.range(0, 5000).toArray()));
        for (User user : users) {
            System.out.println(user.getId());
        }
    }

    @Transactional
    public void batchAdd2() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
//        commandInvoker.add(new UserDeleteCmd());
        Stopwatch stopwatch = Stopwatch.createStarted();
        execute(new UserBatchCreate2Cmd(IntStream.range(0, 5000).toArray()));
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd3() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
//        commandInvoker.add(new UserDeleteCmd());
        Stopwatch stopwatch = Stopwatch.createStarted();
        execute(new UserBatchCreate3Cmd(IntStream.range(0, 5000).toArray()));
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd4() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserDeleteCmd());
        List<User> users = executeReturn(new UserBatchCreate4Cmd(IntStream.range(0, 20000).toArray()));
        for (User user : users) {
            System.out.println(user.getId());
        }
    }

    @Transactional
    public void batchAdd5() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        execute(new UserBatchCreate5Cmd(IntStream.range(0, 20000).toArray()));
    }
}
