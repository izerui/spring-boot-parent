package com.yj2025.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
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
//        String sql = "UPDATE `price_center`.`price_range_purchase` AS pr, `price_center`.`inventory_price_purchase` AS ip  SET pr.begin_num = pr.begin_num * ip.valuation_ratio, pr.original_unit_price = pr.original_unit_price * ip.valuation_ratio, pr.rmb_unit_price = pr.rmb_unit_price * ip.valuation_ratio  WHERE \tpr.record_id = '140a9c7c-6778-4b27-88af-40b5b7fd6e52'  \tAND pr.price_record_id = ip.record_id";
        Statement parse = CCJSqlParserUtil.parse(sql);
        parse.accept(new StatementVisitorAdapter() {

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

    @Test
    public void testSql2() throws SqlParseException {
        String sql = "update test_user set age = 18 where age > 16 and code is not null";
//        String sql = "UPDATE price_center.price_range_purchase AS pr, price_center.inventory_price_purchase AS ip  SET pr.begin_num = pr.begin_num * ip.valuation_ratio, pr.original_unit_price = pr.original_unit_price * ip.valuation_ratio, pr.rmb_unit_price = pr.rmb_unit_price * ip.valuation_ratio  WHERE \tpr.record_id = '140a9c7c-6778-4b27-88af-40b5b7fd6e52'  \tAND pr.price_record_id = ip.record_id";
        SqlParser sqlParser = SqlParser.create(sql);
        SqlUpdate sqlUpdate = (SqlUpdate) sqlParser.parseStmt();
//        System.out.println(sqlUpdate.toSqlString(c -> {
//            return c.withDialect(AnsiSqlDialect.DEFAULT)
//                    .withAlwaysUseParentheses(false)
//                    .withSelectListItemsOnSeparateLines(false)
//                    .withUpdateSetListNewline(false)
//                    .withIndentation(0);
//        }));
        System.out.println(sqlUpdate.toString().split("\n")[0]);
//        System.out.println(sqlUpdate.getTargetTable().toString());
//        System.out.println(sqlUpdate.getCondition().toString());
//        System.out.println(sqlUpdate.getOperandList());

    }


    @Test
    public void test03() {
        while (true) {
            List<Callable<Integer>> collect = IntStream.range(0, 10).mapToObj(value -> new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return value;
                }
            }).collect(Collectors.toList());
            List<Integer> integers = Context.submitAsyncWaitReturn(5, 10, Duration.ofSeconds(60), collect);
            log.info("end.....{}", integers);
            Assert.state(integers.size() == collect.size(), "list大小不一致");
            log.info("--------------------------------------------");
            Context.tryWith(() -> Thread.sleep(3000));
        }


    }


}
