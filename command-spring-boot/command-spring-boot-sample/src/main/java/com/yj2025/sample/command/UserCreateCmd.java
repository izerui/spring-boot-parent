package com.yj2025.sample.command;


import com.yj2025.command.AbstractCommand;
import com.yj2025.command.Context;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.repository.UserRepository;
import org.springframework.util.Assert;

public class UserCreateCmd extends AbstractCommand<Long> {

    private User user;

    public UserCreateCmd(User user) {
        this.user = user;
    }

    @Override
    protected void beforeDoExecute() {
        if (user == null) {
            throw new RuntimeException("user对象不能为空");
        }
        Assert.state(user.getId() == null, "新增用户，id必须为空");
    }

    @Override
    protected Long doExecute() throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        userRepository.save(user);
        return user.getId();
    }


}
