package com.yj2025.sample;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.service.TestUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class)
@Transactional
@Rollback(value = false)
public class TestUserTest {

    @Autowired
    private TestUserService testUserService;

    @Test
    public void testFindAll() {
        Iterable<TestUser> all = testUserService.findAll();
        log.info(all.toString());
    }

    @Test
    public void testPage() {
        Page<TestUser> byPage = testUserService.findByPage(PageRequest.of(0, 55));
        System.out.println(byPage);
    }
}
