package com.yj2025.sample.command;


import com.google.common.base.Supplier;
import com.yj2025.command.AbstractCommand;
import com.yj2025.command.Context;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.repository.UserRepository;
import org.springframework.util.Assert;

public class UserCreateCmd extends AbstractCommand<User, Long> {

    public UserCreateCmd(User parameter) {
        super(parameter);
    }

    @Override
    protected void validatingBeforeExecute(User parameter) {
        if (parameter == null) {
            throw new RuntimeException("user对象不能为空");
        }
        Assert.state(parameter.getId() == null, "新增用户，id必须为空");
    }

    @Override
    protected Long doExecute(User parameter) throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        userRepository.save(parameter);
        return parameter.getId();
    }


}
