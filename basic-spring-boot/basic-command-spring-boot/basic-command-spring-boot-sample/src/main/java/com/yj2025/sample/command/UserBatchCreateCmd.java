package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.sample.entity.JpaUser;
import com.yj2025.sample.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBatchCreateCmd extends BasicCommand<List<JpaUser>> {

    private int[] integers;

    public UserBatchCreateCmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected List<JpaUser> doExecute() throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        List<JpaUser> userlist = Arrays.stream(integers).mapToObj(operand -> {
            JpaUser user = new JpaUser();
            user.setCode("code" + operand);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            user.setEntCode("ent001");
            return user;
        }).collect(Collectors.toList());
        userRepository.saveAll(userlist);
        return userlist;
    }
}
