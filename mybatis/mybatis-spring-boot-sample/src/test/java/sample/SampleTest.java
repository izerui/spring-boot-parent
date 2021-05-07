package sample;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj2025.sample.Application;
import com.yj2025.sample.entity.User;
import com.yj2025.sample.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

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
        Page<User> page = new Page<>(1,2);
        Page<User> pageResult = userMapper.selectPage(page, null);
        System.out.println(pageResult.getTotal());
    }

    @Test
    public void insertSample() {
        User user = new User();
        user.setName(UUID.randomUUID().toString());
        user.setEntCode("878");
        user.setAge(11);
        user.setEmail(UUID.randomUUID().toString());
        userMapper.insert(user);
    }

}
