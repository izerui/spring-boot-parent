package com.yj2025.sample;

import com.yj2025.basic.support.CacheWrapperAware;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ValueWrapperTest {


    public static void main(String[] args) {
        MyDTO dto = MyDTO.builder()
                .code("code1")
                .build()
                .wrap("code", "name", myDTO -> {
                    return myDTO.code + " ----- name";
                });
        System.out.println(dto);


        // list

        List<MyDTO> dtos = new ArrayList<>(){{
            add(new MyDTO.MyDTOBuilder().code(UUID.randomUUID().toString()).build());
            add(new MyDTO.MyDTOBuilder().code(UUID.randomUUID().toString()).build());
            add(new MyDTO.MyDTOBuilder().code(UUID.randomUUID().toString()).build());
            add(new MyDTO.MyDTOBuilder().code(UUID.randomUUID().toString()).build());
        }};
        dtos.forEach(myDTO -> myDTO.wrap("code", "name", myDTO1 -> {
            return myDTO.code + " ++++++ name";
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
