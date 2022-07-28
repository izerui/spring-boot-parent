package junit;

import com.google.common.base.Stopwatch;
import com.yj2025.basic.support.Context;
import com.yj2025.jpa.Application;
import com.yj2025.jpa.entity.Abcd;
import com.yj2025.jpa.entity.User;
import com.yj2025.jpa.entity.UserDistinct;
import com.yj2025.jpa.impl.Conditions;
import com.yj2025.jpa.repository.AbcdRepository;
import com.yj2025.jpa.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@Rollback(value = false)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestJunit {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AbcdRepository abcdRepository;
    @Autowired
    private DataSource dataSource;

    @Test
    public void testSelectSql(){
        Conditions conditions = Conditions.where("name").is("张无忌");
        List<Object[]> list = (List<Object[]>) userRepository.selectSql("select sum(age),sum(version) from User ",conditions);
        Object[] array = list.get(0);
        for (Object o : array) {
            System.out.println("------"+o);
        }
    }

    @Test
    public void lis22t() {
        List<User> all = userRepository.findAll();
        for (User user : all) {
            user.setAge(18);
        }
        userRepository.batchUpdate(all);
        System.out.println(all);
    }


    @Test
    public void add() {
        userRepository.deleteAll();
        for (int i = 200; i < 220; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            userRepository.save(user);
        }
    }

    /**
     * PHASE 1 : Parse the HQL into an AST. 解析 hql
     * {@link org.hibernate.hql.internal.ast.QueryTranslatorImpl##doCompile(Map, boolean, String)}
     */
    @Test
    public void testConditions() {
        Conditions where = Conditions.where("a =1 and b = 1 or (a=2 and b = 3)");
        List<Abcd> all = abcdRepository.findAll(where);
        System.out.println(where.toString());
    }

    @Test
    public void saveEntity() {
        User user = new User();
        user.setId(61L);
        user.setCreateTime(new Date());
        user.setCode("22");
        user.setName("22");
        user.setEmail("22");
        user.setAge(22);
        userRepository.save(user);
    }

    @Test
    public void saveEntity2() {
        User one = userRepository.getOne(61L);
        one.setId(60L);
        one.setName("吃饭");
        userRepository.save(one);
    }

    @Test
    public void list() {
        List<User> all = userRepository.findAll();
        System.out.println(all.toArray());
        Integer id = userRepository.max("age", Integer.class);
        System.out.println(id);
    }

    @Test
    public void distinct() {
        List<UserDistinct> userDistincts = userRepository.distinctAll(UserDistinct.class);
        System.out.println(userDistincts.toArray());
    }

    @Test
    public void distinctPage() {
        Page<UserDistinct> userDistincts = userRepository.distinctAll(Conditions.empty(), PageRequest.of(0, 10), UserDistinct.class);
        System.out.println(userDistincts);
    }

    @Test
    public void testGroupLimit() {
        List<Map> maps = userRepository.groupAll(Lists.newArrayList("code"), Lists.newArrayList("code"), Map.class, 3);
        System.out.println(maps);
    }

    @Test
    public void batchInsert() {
        Stopwatch watch = Stopwatch.createStarted();
        List<User> users = IntStream.range(0, 5000).mapToObj(value -> {
            User user = new User();
            user.setVersion(0);
            user.setCreateTime(new Date());
            user.setCode("code" + value);
            user.setName("name" + value);
            user.setEmail("email" + value);
            user.setAge(20);
            return user;
        }).collect(Collectors.toList());
        userRepository.batchInsert(users);
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void batchInsert2() {
        Stopwatch watch = Stopwatch.createStarted();
        BatchSqlUpdate batchSqlExecutor = Context.batchUpdate(
                dataSource,
                "insert into test_user(version, create_time, code, name, email, age) values (?,?,?,?,?,?)",
                List.of(JDBCType.NUMERIC, JDBCType.TIMESTAMP, JDBCType.VARCHAR, JDBCType.VARCHAR, JDBCType.VARCHAR, JDBCType.NUMERIC),
                500);
        IntStream.range(0, 5000).forEach(value -> {
            batchSqlExecutor.update(0, new Date(), "code" + value, "name" + value, "email" + value, 20);
        });
        batchSqlExecutor.flush();
        batchSqlExecutor.reset();
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void batchInsert3() {
        Stopwatch watch = Stopwatch.createStarted();
        List<User> users = IntStream.range(0, 5000).mapToObj(value -> {
            User user = new User();
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
    public void batchInsert4() {
        Stopwatch watch = Stopwatch.createStarted();
        List<User> users = IntStream.range(0, 5000).mapToObj(value -> {
            User user = new User();
            user.setVersion(0);
            user.setCreateTime(new Date());
            user.setCode("code" + value);
            user.setName("name" + value);
            user.setEmail("email" + value);
            user.setAge(29);
            return user;
        }).collect(Collectors.toList());
        int[] ints = Context.batchUpdate(dataSource, "insert into test_user(version, create_time, code, name, email, age) values (:version,:createTime,:code,:name,:email,:age)", users);
        System.out.println(ints);
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void batchInsert5() {
        Stopwatch watch = Stopwatch.createStarted();
        List<User> users = IntStream.range(0, 5000).mapToObj(value -> {
            User user = new User();
            user.setVersion(0);
            user.setCreateTime(new Date());
            user.setCode("code" + value);
            user.setName("name" + value);
            user.setEmail("email" + value);
            user.setAge(29);
            return user;
        }).collect(Collectors.toList());
        int[] ints = Context.batchInsert(dataSource, "test_user", users);
        System.out.println(ints);
        System.out.println("耗时: " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    public static void main(String[] args) {
        Conditions conditions = Conditions.where("a").is(1)
                .and("b").is(2)
                .or(
                        Conditions.where("c").like(null)
                )
                .and(
                        Conditions.where("e").is(5).or("f").is(6)
                );
        conditions.and("ddd").is(null).and("fff").like("");


        System.out.println(
                conditions
        );
    }

}
