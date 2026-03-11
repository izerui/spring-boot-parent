package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.entity.JdbcUser;
import com.yj2025.sample.repository.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JdbcUserInsertCmd extends BasicCommand<Void> {

    private final List<JdbcUser> users;

    public JdbcUserInsertCmd(List<JdbcUser> users) {
        this.users = users;
    }

    @Override
    protected Void doExecute() throws Exception {
        jdbcUserRepository.saveAll(users);
        return null;
    }

    @Autowired
    private JdbcUserRepository jdbcUserRepository;
}
