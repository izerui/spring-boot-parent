package sample;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.javafaker.Faker;
import com.yj2025.sample.Application;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Locale;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SampleTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testPage() {
        Page<User> page = new Page<>(1, 50);
        Page<User> pageResult = userMapper.selectPage(page, null);
        System.out.println(pageResult.getTotal());
    }

    @Test
    public void testPage2() {
        PageRequest pageRequest = PageRequest.of(0, 35, Sort.Direction.DESC,"name");
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.orderByDesc(User::getEmail);
        org.springframework.data.domain.Page<User> page = userMapper.selectPage(pageRequest, wrapper);
        System.out.println(page.getTotalElements());
    }

    @Test
    public void test002() {
        System.out.println("");
    }

    @Test
    public void insertDemoData() {
        Faker faker = new Faker(Locale.CHINA);
        int size = 333;
        for (int i = 0; i < size; i++) {
            User user = new User();
            user.setName(faker.name().name());
            user.setEntCode(faker.code().ean8());
            user.setAge(faker.number().numberBetween(10, 100));
            user.setEmail(faker.address().city());
            userMapper.insert(user);
        }
    }


    @Test
    public void testSearch() {
        Long aLong = userMapper.selectCount(Wrappers.lambdaQuery(User.class).gt(User::getAge, 10).eq(User::getEntCode, "213"));
        log.info("{}", aLong);
    }

}
