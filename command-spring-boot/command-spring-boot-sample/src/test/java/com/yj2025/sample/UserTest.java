package com.yj2025.sample;

import com.yj2025.sample.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class)
public class UserTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreate() {
        userService.add();
    }
}
