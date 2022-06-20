package com.yj2025.sample.command;

import com.google.common.collect.Lists;
import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.command.Context;
import com.yj2025.sample.entity.User;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBatchCreate4Cmd extends BasicCommand<List<User>> {

    private int[] integers;

    public UserBatchCreate4Cmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected List<User> doExecute() throws Exception {
        List<User> userlist = Arrays.stream(integers).mapToObj(operand -> {
            User user = new User();
            user.setCode("code" + operand);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            return user;
        }).collect(Collectors.toList());
        EntityManager entityManager = Context.getBean(EntityManager.class);
        List<List<User>> partition = Lists.partition(userlist, 500);
        for (List<User> users : partition) {
            for (User user : users) {
                entityManager.persist(user);
            }
            entityManager.flush();
            entityManager.clear();
        }
        return userlist;
    }
}
