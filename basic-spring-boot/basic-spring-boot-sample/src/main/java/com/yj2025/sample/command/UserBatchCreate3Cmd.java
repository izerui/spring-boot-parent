package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.performance.Consumer;
import com.yj2025.performance.Producer;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.repository.UserRepository;

public class UserBatchCreate3Cmd extends BasicCommand<Void> {

    private int[] integers;

    public UserBatchCreate3Cmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected Void doExecute() throws Exception {
        UserRepository userRepository = Context.getBean(UserRepository.class);
        Producer<User> producer = Context.multiConsumer(5, new Consumer<User>() {
            @Override
            protected void handlerEvent(User event) throws Exception {
                userRepository.save(event);
            }
        });

        for (Integer integer : integers) {
            User user = new User();
            user.setId(null);
            user.setCode("code" + integer);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            producer.sendData(user);
        }
        producer.shutdown();
        return null;
    }
}
