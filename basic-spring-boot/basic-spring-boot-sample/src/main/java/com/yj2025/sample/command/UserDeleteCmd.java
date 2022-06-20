package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.command.Context;
import com.yj2025.sample.repository.UserRepository;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class UserDeleteCmd extends BasicCommand<Void> {

    @Override
    protected Void doExecute() throws Exception{
        Context.getBean(UserRepository.class).deleteAll();
        return null;
    }

}
