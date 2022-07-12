package com.yj2025.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.basic.support.Context;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.util.Map;

public class JunitTest {
    
    @Test
    public void test01() {
        Context.tryWith(() -> {
            new ObjectMapper().readValue("22ssssss2", Map.class);
        });
    }

    @Test
    public void test02() {
        Context.tryWith(() -> {
            DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
            ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(defaultResourceLoader);
            MetadataReaderFactory metaReader = new CachingMetadataReaderFactory(defaultResourceLoader);
            Resource[] resources = resolver.getResources("classpath*:com/yj2025/**/*Cmd.class");

            for (Resource r : resources) {
                MetadataReader reader = metaReader.getMetadataReader(r);
                System.out.println(reader.getClassMetadata().getClassName());
            }
        });
    }


}
