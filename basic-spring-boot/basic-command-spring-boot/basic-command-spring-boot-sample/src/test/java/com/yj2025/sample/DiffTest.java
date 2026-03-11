package com.yj2025.sample;

import com.github.dadiyang.equator.FieldInfo;
import com.yj2025.basic.support.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class DiffTest {

    @Test
    public void testDiff01() {
        A a1 = new A(1,"a1", new B(1,"b1", 11));
        A a2 = new A(1,"a1", new B(1,"b2", 11));

        List<FieldInfo> diff = Context.diff(a1, a2);
        diff.forEach(fieldInfo -> {
            log.info("\nfield:{} \nvalue1:{} \nvalue2:{}", fieldInfo.getFieldName(),fieldInfo.getFirstVal(),fieldInfo.getSecondVal());
        });

    }


    @AllArgsConstructor
    @Data
    public static class A {
        private Integer id;
        private String name;
        private B b;
    }

    @AllArgsConstructor
    @Data
    public static class B {
        private Integer id;
        private String name;
        private Integer age;
    }
}
