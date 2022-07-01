package com.yj2025.sample;

import com.google.common.base.Stopwatch;
import com.yj2025.basic.support.Context;
import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.repository.TestUserRepository;
import com.yj2025.sample.service.TestUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class)
@Transactional
@Rollback(value = false)
public class TestUserTest {

    @Autowired
    private TestUserService testUserService;
    @Autowired
    private TestUserRepository userRepository;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

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

    @Test
    public void testInsert() {
        Stopwatch watch = Stopwatch.createStarted();
        List<TestUser> users = IntStream.range(0, 5000).mapToObj(value -> {
            TestUser user = new TestUser();
            user.setVersion(0);
            user.setCreateTime(new Date());
            user.setCode("code" + value);
            user.setName("name" + value);
            user.setEmail("email" + value);
            user.setAge(20);
            return user;
        }).collect(Collectors.toList());
        userRepository.saveAll(users);
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testInsert2() {
        Stopwatch watch = Stopwatch.createStarted();
        List<Map<String, Object>> users = IntStream.range(0, 5000).mapToObj(value -> {
            Map<String, Object> map = new HashMap<>();
            map.put("version", 0);
            map.put("create_time", new Date());
            map.put("code", "code" + value);
            map.put("name", "name" + value);
            map.put("email", "email" + value);
            map.put("age", 28);
            return map;
        }).collect(Collectors.toList());
        jdbcTemplate.batchUpdate("insert into test_user(version, create_time, code, name, email, age) values (:version,:create_time,:code,:name,:email,:age)",
                users.toArray(new HashMap[users.size()]));
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testInsert3() {
        Stopwatch watch = Stopwatch.createStarted();
        Map<String, Object> map = new HashMap<>();
        map.put("version", 0);
        map.put("create_time", new Date());
        map.put("code", "code" + 2);
        map.put("name", "name" + 2);
        map.put("email", "email" + 2);
        map.put("age", 33);
        Number test_user = Context.insertReturnKey(dataSource, "test_user", "id", map);
        System.out.println("首次耗时：" + watch.elapsed(TimeUnit.MILLISECONDS));
        System.out.println("返回主键值：" + test_user);

        watch.reset();
        watch.start();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("version", 0);
        map2.put("create_time", new Date());
        map2.put("code", "code" + 2);
        map2.put("name", "name" + 2);
        map2.put("email", "email" + 2);
        map2.put("email2", "email" + 2);
        map2.put("age", 33);
        Context.insert(dataSource, "test_user", map2);
        System.out.println("二次插入耗时：" + watch.elapsed(TimeUnit.MILLISECONDS));
        watch.reset();
        watch.start();
        List<TestUser> users = IntStream.range(0, 5000).mapToObj(value -> {
            TestUser user = new TestUser();
            user.setVersion(0);
            user.setCreateTime(new Date());
            user.setCode("code" + value);
            user.setName("name" + value);
            user.setEmail("email" + value);
            user.setAge(66);
            return user;
        }).collect(Collectors.toList());
        Context.batchInsert(dataSource, "test_user", users);
        System.out.println("批量插入5000条耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testFindByAge() {
//        System.out.println(byAge);
    }

}
