package com.yj2025.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.basic.support.Context;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class JunitTest {
    
    @Test
    public void test01() {
        Context.tryWith(() -> {
            new ObjectMapper().readValue("22ssssss2", Map.class);
        });
    }
}
