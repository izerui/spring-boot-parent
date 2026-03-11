package com.yj2025.sample.command;

import com.yj2025.basic.command.BasicCommand;
import com.yj2025.sample.entity.JdbcUser;
import com.yj2025.sample.repository.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

public class JdbcUserQueryCmd extends BasicCommand<Page<JdbcUser>> {

    private final String entCode;
    private final Integer age;

    public JdbcUserQueryCmd(String entCode, Integer age) {
        this.entCode = entCode;
        this.age = age;
    }


    @Override
    protected Page<JdbcUser> doExecute() throws Exception {
        Criteria criteria = Criteria
                .where("ent_code").is(entCode)
                .and("age").greaterThan(age);
        return jdbcUserRepository.findAll(Query.query(criteria), PageRequest.of(0, 100));
    }

    @Autowired
    private JdbcUserRepository jdbcUserRepository;
}
