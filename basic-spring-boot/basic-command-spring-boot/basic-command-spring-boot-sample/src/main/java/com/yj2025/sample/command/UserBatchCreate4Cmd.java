package com.yj2025.sample.command;

import com.google.common.collect.Lists;
import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.sample.entity.JpaUser;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBatchCreate4Cmd extends BasicCommand<List<JpaUser>> {

    private int[] integers;

    public UserBatchCreate4Cmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected List<JpaUser> doExecute() throws Exception {
        List<JpaUser> userlist = Arrays.stream(integers).mapToObj(operand -> {
            JpaUser user = new JpaUser();
            user.setCode("code" + operand);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            return user;
        }).collect(Collectors.toList());
        EntityManager entityManager = Context.getBean(EntityManager.class);
        List<List<JpaUser>> partition = Lists.partition(userlist, 500);
        for (List<JpaUser> users : partition) {
            for (JpaUser user : users) {
                entityManager.persist(user);
            }
            entityManager.flush();
            entityManager.clear();
        }
        return userlist;
    }
}
