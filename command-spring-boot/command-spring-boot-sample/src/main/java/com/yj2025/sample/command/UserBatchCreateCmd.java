package com.yj2025.sample.command;

import com.yj2025.command.AbstractCommand;
import com.yj2025.command.Context;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBatchCreateCmd extends AbstractCommand<Void> {

    private int[] integers;

    public UserBatchCreateCmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected Void doExecute() throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        List<User> userlist = Arrays.stream(integers).mapToObj(operand -> {
            User user = new User();
            user.setCode("code" + operand);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            return user;
        }).collect(Collectors.toList());
        userRepository.saveAll(userlist);
        return null;
    }
}
