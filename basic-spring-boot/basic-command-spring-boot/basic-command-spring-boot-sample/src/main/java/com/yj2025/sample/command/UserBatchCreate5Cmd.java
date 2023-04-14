package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.basic.support.Context;
import com.yj2025.basic.support.DbContext;
import org.springframework.jdbc.object.BatchSqlUpdate;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UserBatchCreate5Cmd extends BasicCommand<Void> {

    private int[] integers;

    public UserBatchCreate5Cmd(int[] integers) {
        this.integers = integers;
    }

    @Override
    protected Void doExecute() throws Exception {
        DataSource dataSource = Context.getBean(DataSource.class);
        BatchSqlUpdate batchSqlUpdate = DbContext.batchUpdate(
                dataSource,
                "insert into test_user(version,create_time,code,name,email,age) values (?,?,?,?,?,?)",
                List.of(JDBCType.NUMERIC,
                        JDBCType.TIMESTAMP,
                        JDBCType.VARCHAR,
                        JDBCType.VARCHAR,
                        JDBCType.VARCHAR,
                        JDBCType.NUMERIC),
                1000
        );
        Arrays.stream(integers).forEach(operand -> {
            batchSqlUpdate.update(0, new Date(), "code" + operand, "张思峰", "mail00" + operand, operand);
        });
        batchSqlUpdate.flush();
        batchSqlUpdate.reset();
        return null;
    }
}
