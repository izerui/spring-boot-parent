package com.yj2025.sample.service;

import com.yj2025.command.Command;
import com.yj2025.command.CommandInvoker;
import com.yj2025.sample.command.UserCreateCmd;
import com.yj2025.sample.command.UserDeleteCmd;
import com.yj2025.sample.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    public void add() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.add(new UserDeleteCmd());
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            int finalI = i;
            commandInvoker.add(new UserCreateCmd(user), () -> finalI > 5);
        }
        List<Object> results = commandInvoker.execute().getCommands().stream().map(Command::getResult).collect(Collectors.toList());
        for (Object result : results) {
            System.out.println(result);
        }
    }
}
