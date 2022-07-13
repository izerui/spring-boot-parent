package com.yj2025.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.basic.support.Context;
import io.vavr.API;
import io.vavr.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.update.Update;
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
    
    @Test
    public void testSql() throws ParseException, JSQLParserException {
        String sql = "update test_user set age = 18 where age > 16 and code is not null";
        Statement parse = CCJSqlParserUtil.parse(sql);
        parse.accept(new StatementVisitorAdapter(){

            @Override
            public void visit(Update update) {
                System.out.println(update.getWhere().toString());
                System.out.println(update.getTable().getName());
                update.getUpdateSets().forEach(updateSet -> {
                    System.out.println(updateSet.toString());
                });
                System.out.println(update.getUpdateSets().toString());
                super.visit(update);
            }
        });



    }


}
