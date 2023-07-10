package sample;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yj2025.mybatis.toolkit.ReflectionUtil;
import com.yj2025.sample.Application;
import com.yj2025.sample.entity.Simple;
import com.yj2025.sample.mapper.SimpleMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SimpleTest {

    @Autowired
    private SimpleMapper simpleMapper;

    @Test
    public void insertBatchs() {
        simpleMapper.delete(Wrappers.lambdaQuery(Simple.class).gt(Simple::getId, 0));
        IntStream.range(0, 1000).forEach(value -> {
            Simple simple = new Simple();
            simple.setWord("word" + value);
            simple.setSimple("smp" + value);
            if (value >= 100) {
                simple.setType("type100");
            } else {
                simple.setType("type" + value);
            }
            simpleMapper.insert(simple);
        });
    }

    @Test
    public void selectPage() {
        Page<Simple> simples = simpleMapper.selectPage(PageRequest.of(1, 15, Sort.Direction.DESC, "word"), Wrappers.emptyWrapper());
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageOrigin() {
        Page<Simple> simples = simpleMapper.findByOrigin(PageRequest.of(0, 15), "type100");
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageNull() {
        Page<Simple> simples = simpleMapper.selectPage(PageRequest.of(1, 15), null);
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageWrapper() {
        LambdaQueryWrapper<Simple> wrapper = Wrappers.lambdaQuery(Simple.class);
        wrapper.eq(Simple::getType, "type100");
        wrapper.orderByAsc(Simple::getWord);
        Page<Simple> simples = simpleMapper.selectPage(PageRequest.of(0, 15), wrapper);
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageMap() {
        LambdaQueryWrapper<Simple> wrapper = Wrappers.lambdaQuery(Simple.class);
        wrapper.eq(Simple::getType, "type100");
        wrapper.orderByAsc(Simple::getWord);
        Page<Map<String, Object>> simples = simpleMapper.selectMapsPage(PageRequest.of(0, 15), wrapper);
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testSelectOne() {
        Simple simple = simpleMapper.selectOne(Wrappers.lambdaQuery(Simple.class).eq(Simple::getWord, "word136"));
        System.out.println(simple);
    }

    public static void main(String[] args) {
        User u = new User();
        u.setName("111");

        ReflectionUtil.setPropertyValue(User.class, u, "name", "222");
        System.out.println(u.getName());
    }


    @Data
    public static class User {
        private String name;
    }
}
