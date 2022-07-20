package com.yj2025.sample.service;

import com.google.common.base.Stopwatch;
import com.yj2025.basic.command.CommandInvoker;
import com.yj2025.basic.support.WebRequestAware;
import com.yj2025.sample.command.*;
import com.yj2025.sample.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class UserService implements WebRequestAware {

    @Transactional
    public void add() {
        String userName = getUserName();
        System.out.println(userName);
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.add(new UserDeleteCmd());
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张2丰");
            user.setEmail("张三丰@qq.com");
            int finalI = i;
            commandInvoker.add(new UserCreateCmd(user), () -> finalI > 5);
        }
        commandInvoker.execute();
    }

    @Transactional
    public void batchAdd() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.add(new UserDeleteCmd());
        commandInvoker.add(new UserBatchCreateCmd(IntStream.range(0, 5000).toArray()), users -> {
            for (User user : users) {
                System.out.println(user.getId());
            }
        });
        commandInvoker.execute();
    }

    @Transactional
    public void batchAdd2() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
//        commandInvoker.add(new UserDeleteCmd());
        Stopwatch stopwatch = Stopwatch.createStarted();
        commandInvoker.add(new UserBatchCreate2Cmd(IntStream.range(0, 5000).toArray()));
        commandInvoker.execute();
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd3() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
//        commandInvoker.add(new UserDeleteCmd());
        Stopwatch stopwatch = Stopwatch.createStarted();
        commandInvoker.add(new UserBatchCreate3Cmd(IntStream.range(0, 5000).toArray()));
        commandInvoker.execute();
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Transactional
    public void batchAdd4() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.add(new UserDeleteCmd());
        commandInvoker.add(new UserBatchCreate4Cmd(IntStream.range(0, 20000).toArray()), users -> {
            for (User user : users) {
                System.out.println(user.getId());
            }
        });
        commandInvoker.execute();
    }

    @Transactional
    public void batchAdd5() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
//        commandInvoker.add(new UserDeleteCmd());
        commandInvoker.add(new UserBatchCreate5Cmd(IntStream.range(0, 20000).toArray()));
        commandInvoker.execute();
    }
}
