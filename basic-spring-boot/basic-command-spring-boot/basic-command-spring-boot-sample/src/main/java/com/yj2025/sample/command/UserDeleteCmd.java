package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.repository.UserRepository;
import com.yj2025.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class UserDeleteCmd extends BasicCommand<Void> {

    @Autowired
    private UserRepository userRepository;

    @Resource
    private UserService userService;

    private String s;

    public UserDeleteCmd(String s) {
        this.s = s;
        System.out.println("2222");
    }

    @Override
    protected Void doExecute() throws Exception{
        userRepository.deleteAll();
        return null;
    }

}
