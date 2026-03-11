package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.performance.Consumer;
import com.yj2025.performance.Producer;
import com.yj2025.sample.entity.JpaUser;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

public class UserBatchCreate3Cmd extends BasicCommand<Void> {

    private int[] integers;

    public UserBatchCreate3Cmd(int[] integers) {
        super();
        this.integers = integers;
    }

    @Override
    protected Void doExecute() throws Exception {
        Producer<JpaUser> producer = Context.multiConsumer(JpaUser.class, 5, new Consumer<JpaUser>() {
            @Override
            protected void handlerEvent(JpaUser event) throws Exception {
                SimpleJdbcInsert insert = new SimpleJdbcInsert($(DataSource.class));
                insert.setTableName("test_user");
                insert.execute(event.toMap());
                System.out.println(event.getName());
            }
        });

        for (Integer integer : integers) {
            producer.sendData(user -> {
                user.setId(null);
                user.setCode("code" + integer);
                user.setName("张三丰");
                user.setEmail("张三丰@qq.com");
            });
//            Thread.sleep(10);
        }
        producer.shutdown();
        return null;
    }

    @Override
    protected long getLimitWarnningTimeMillis() {
        return 1000;
    }
}
