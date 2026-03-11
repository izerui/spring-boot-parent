package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.entity.MyUser;
import com.yj2025.sample.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class MyUserFindCmd extends BasicCommand<Page<MyUser>> {

    private final String entCode;

    public MyUserFindCmd(String entCode) {
        this.entCode = entCode;
    }


    @Override
    protected Page<MyUser> doExecute() throws Exception {
        return userMapper.findByOrigin(entCode, PageRequest.of(0, 200));
    }

    @Autowired
    private UserMapper userMapper;
}
