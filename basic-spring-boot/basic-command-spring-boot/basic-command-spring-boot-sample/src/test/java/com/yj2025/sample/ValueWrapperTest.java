package com.yj2025.sample;

import com.yj2025.basic.support.CacheWrapperAware;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ValueWrapperTest {


    @Test
    public void testField() {
        List<MyDTO> dtos = new ArrayList<>() {{
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
        }};
        dtos.forEach(myDTO -> myDTO.wrapByField("code","name", myDTO1 -> {
            System.out.println("执行了一次");
            return myDTO1.code + " ----- name";
        }));
        System.out.println(dtos);
    }


    @Test
    public void testMethod() {
        List<MyDTO> dtos = new ArrayList<>() {{
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
            add(new MyDTO.MyDTOBuilder().code("123").build());
        }};
        dtos.forEach(myDTO -> myDTO.wrapByMethod("getCode", myDTO1 -> {
            System.out.println("执行了一次");
            return myDTO1.code + " ----- name";
        }, s -> {
            myDTO.setName(s);
        }));
        System.out.println(dtos);
    }

    @Builder
    @Data
    @ToString
    public static class MyDTO implements CacheWrapperAware<MyDTO> {
        private String code;
        private String name;
    }
}
