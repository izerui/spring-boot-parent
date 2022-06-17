package com.yj2025.sample.command;

import com.yj2025.command.AbstractCommand;
import com.yj2025.command.Context;
import com.yj2025.sample.repository.UserRepository;

public class UserDeleteCmd extends AbstractCommand<Void> {

    @Override
    protected Void doExecute() throws Exception {
        Context.getBean(UserRepository.class).deleteAll();
        return null;
    }
}
