package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.command.BasicVoidCommand;
import com.yj2025.sample.repository.UserRepository;
import com.yj2025.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

@ThreadSafe
public class UserDeleteCmd extends BasicVoidCommand {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private String s;

    public UserDeleteCmd(String s) {
        this.s = s;
        System.out.println("2222");
    }

    @Override
    protected void perform() throws Exception {
        userRepository.deleteAll();
    }

}
