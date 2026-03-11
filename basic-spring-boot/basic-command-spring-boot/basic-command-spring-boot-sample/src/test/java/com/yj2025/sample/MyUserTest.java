package com.yj2025.sample;

import com.yj2025.sample.entity.MyUser;
import com.yj2025.sample.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;

@Slf4j
@SpringBootTest(classes = SampleApplication.class)
public class MyUserTest {

    @SpyBean
    private UserService userService;

    @Test
    public void add() {
        userService.addMyUser();
    }

     @Test
    public void find() {
         Page<MyUser> myUser = userService.findMyUser();
         System.out.println(myUser);
     }
}
