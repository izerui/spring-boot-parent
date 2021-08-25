package junit;

import com.yj2025.jpa.Application;
import com.yj2025.jpa.entity.User;
import com.yj2025.jpa.entity.UserDistinct;
import com.yj2025.jpa.impl.Conditions;
import com.yj2025.jpa.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestJunit {

    @Autowired
    UserRepository userRepository;


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

    @Test
    public void testConditions() {
        Conditions conditions = Conditions.where("entCode").is("11111").and("purchaseType").is(1).remove("entCode");
        System.out.println(conditions);
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
