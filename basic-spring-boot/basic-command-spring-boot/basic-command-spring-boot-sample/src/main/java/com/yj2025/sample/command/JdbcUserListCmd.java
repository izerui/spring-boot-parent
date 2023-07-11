package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.entity.JdbcUser;
import com.yj2025.sample.repository.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JdbcUserListCmd extends BasicCommand<List<JdbcUser>> {

    private final String entCode;

    public JdbcUserListCmd(String entCode) {
        this.entCode = entCode;
    }


    @Override
    protected List<JdbcUser> doExecute() throws Exception {
        return jdbcUserRepository.findList(entCode, "100");
    }

    @Autowired
    private JdbcUserRepository jdbcUserRepository;
}
