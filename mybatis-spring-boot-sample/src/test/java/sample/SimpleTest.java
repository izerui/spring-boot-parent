package sample;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yj2025.sample.Application;
import com.yj2025.sample.entity.Simple;
import com.yj2025.sample.mapper.SimpleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SimpleTest {

    @Autowired
    private SimpleMapper simpleMapper;

    @Test
    public void selectPage() {
        Page<Simple> simples = simpleMapper.selectPage(PageRequest.of(1, 15), Wrappers.emptyWrapper());
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageOrigin() {
        Page<Simple> simples = simpleMapper.findByOrigin(PageRequest.of(1, 15), "n.");
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
        wrapper.eq(Simple::getType,"n.");
        wrapper.orderByAsc(Simple::getWord);
        Page<Simple> simples = simpleMapper.selectPage(PageRequest.of(0, 15), wrapper);
        System.out.println(simples.getTotalElements());
    }

    @Test
    public void testPageMap() {
        LambdaQueryWrapper<Simple> wrapper = Wrappers.lambdaQuery(Simple.class);
        wrapper.eq(Simple::getType,"n.");
        wrapper.orderByAsc(Simple::getWord);
        Page<Map<String, Object>> simples = simpleMapper.selectMapsPage(PageRequest.of(0, 15), wrapper);
        System.out.println(simples.getTotalElements());
    }
}
