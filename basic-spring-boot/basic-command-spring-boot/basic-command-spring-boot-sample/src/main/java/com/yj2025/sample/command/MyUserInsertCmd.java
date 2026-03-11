package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.entity.MyUser;
import com.yj2025.sample.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyUserInsertCmd extends BasicCommand<Void> {

    private final List<MyUser> users;

    public MyUserInsertCmd(List<MyUser> users) {
        this.users = users;
    }

    @Override
    protected Void doExecute() throws Exception {
        for (MyUser user : users) {
            userMapper.insert(user);
        }
        return null;
    }

    @Autowired
    private UserMapper userMapper;
}
