package com.yj2025.sample;

import com.google.common.base.Stopwatch;
import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.repository.TestUserRepository;
import com.yj2025.sample.service.TestUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
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
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testFindAll() {
        Iterable<TestUser> all = testUserService.findAll("copy1");
        log.info(all.toString());
    }

    @Test
    public void testPage() {
        Page<TestUser> byPage = testUserService.findByPage("copy1", PageRequest.of(0, 55));
        System.out.println(byPage);
    }
    @Test
    public void testList() {
        List<TestUser> copy1 = userRepository.findList("copy1", "code10");
        System.out.println(copy1);
    }

    @Test
    public void testFindCode() {
        List<TestUser> users = testUserService.findByCode("copy1", "code10");
        System.out.println(users);
    }

    @Test
    public void testSave() {
        Stopwatch watch = Stopwatch.createStarted();
        TestUser user = new TestUser();
        user.setVersion(0);
        user.setCreateTime(new Date());
        user.setCode("code" + UUID.randomUUID().toString());
        user.setName("name" + UUID.randomUUID().toString());
        user.setEmail("email" + UUID.randomUUID().toString());
        user.setEntCode("copy1");
        user.setAge(20);
        testUserService.insertUser(user);
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSpel() {
        String nameStr = "test_user_#{T(java.lang.Math).random()}";

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new BeanFactoryAccessor());
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        context.setRootObject(applicationContext);

        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(nameStr, ParserContext.TEMPLATE_EXPRESSION);
        String value = expression.getValue(context, String.class);
        System.out.println(value);
    }

    @Test
    public void testSaveAll() {
        List<Map<String, Object>> users = IntStream.range(0, 18000).mapToObj(value -> {
            Map<String, Object> map = new HashMap<>();
            map.put("version", 0);
            map.put("create_time", new Date());
            map.put("code", "code" + value);
            map.put("name", "name" + value);
            map.put("email", "email" + value);
            map.put("age", 28);
            return map;
        }).collect(Collectors.toList());
        Stopwatch watch = Stopwatch.createStarted();
        jdbcTemplate.batchUpdate("insert into test_user(version, create_time, code, name, email, age) values (:version,:create_time,:code,:name,:email,:age)",
                users.toArray(new HashMap[users.size()]));
        System.out.println("jdbcTemplate.batchUpdate 耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));

        System.out.println("总数: " + userRepository.count());;

    }

//    @Test
//    public void testInsert3() {
//        Stopwatch watch = Stopwatch.createStarted();
//        Map<String, Object> map = new HashMap<>();
//        map.put("version", 0);
//        map.put("create_time", new Date());
//        map.put("code", "code" + 2);
//        map.put("name", "name" + 2);
//        map.put("email", "email" + 2);
////        map.put("age", 33);
//        Number test_user = DbContext.insertReturnKey(dataSource, "test_user", map, "id","age");
//        System.out.println("首次耗时：" + watch.elapsed(TimeUnit.MILLISECONDS));
//        System.out.println("返回主键值：" + test_user);
//
//        watch.reset();
//        watch.start();
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("version", 0);
//        map2.put("create_time", new Date());
//        map2.put("code", "code" + 2);
//        map2.put("name", "name" + 2);
//        map2.put("email", "email" + 2);
//        map2.put("email2", "email" + 2);
//        map2.put("age", 33);
//        DbContext.insert(dataSource, "test_user", map2);
//        System.out.println("二次插入耗时：" + watch.elapsed(TimeUnit.MILLISECONDS));
//        watch.reset();
//        watch.start();
//        List<TestUser> users = IntStream.range(0, 5000).mapToObj(value -> {
//            TestUser user = new TestUser();
//            user.setVersion(0);
//            user.setCreateTime(new Date());
//            user.setCode("code" + value);
//            user.setName("name" + value);
//            user.setEmail("email" + value);
//            user.setAge(66);
//            return user;
//        }).collect(Collectors.toList());
//        DbContext.batchInsert(dataSource, "test_user", users);
//        System.out.println("批量插入5000条耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
//    }

    @Test
    public void testFindByAge() {
//        System.out.println(byAge);
    }

}
