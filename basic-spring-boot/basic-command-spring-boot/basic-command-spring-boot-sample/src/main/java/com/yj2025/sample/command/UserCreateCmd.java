package com.yj2025.sample.command;


import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.sample.entity.JpaUser;
import com.yj2025.sample.repository.UserRepository;

public class UserCreateCmd extends BasicCommand<Long> {

    private JpaUser user;

    public UserCreateCmd(JpaUser user) {
        this.user = user;
    }

    @Override
    protected void beforeDoExecute() {
        validateAndThrow(user);

//        if (user == null) {
//            throw new RuntimeException("user对象不能为空");
//        }
//        Assert.state(user.getId() == null, "新增用户，id必须为空");
    }

    @Override
    protected Long doExecute() throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        userRepository.save(user);
        return user.getId();
    }

}
