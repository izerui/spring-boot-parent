package com.yj2025.sample;

import com.yj2025.sample.entity.JdbcUser;
import com.yj2025.sample.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

@Slf4j
@SpringBootTest(classes = SampleApplication.class)
public class JdbcUserTest {

    @SpyBean
    private UserService userService;

    @Test
    public void add() {
        userService.addJdbcUser("ent001");
    }

    @Test
    public void findList() {
        List<JdbcUser> users = userService.findByList("ent001");
        System.out.println(users);
    }
}
