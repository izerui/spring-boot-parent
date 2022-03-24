package junit;

import com.yj2025.jpa.Application;
import com.yj2025.jpa.PlatformJpaRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    PlatformJpaRepository<User, Long> userLongPlatformJpaRepository;


    @Test
    public void lis22t() {
        List<User> all = userLongPlatformJpaRepository.findAll();
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
     * {@link org.hibernate.hql.internal.ast.QueryTranslatorImpl#doCompile(Map, boolean, String)}
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

    public static void main(String[] args) {
        Conditions conditions = Conditions.where("a").is(1)
                .and("b").is(2)
                .or(
                        Conditions.where("c").is(3).or("d").is(4)
                )
                .and(
                        Conditions.where("e").is(5).or("f").is(6)
                );
        System.out.println(conditions);
    }

}
