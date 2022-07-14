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
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.dialect.AnsiSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.hibernate.loader.custom.sql.SQLQueryParser;
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
//        String sql = "UPDATE `price_center`.`price_range_purchase` AS pr, `price_center`.`inventory_price_purchase` AS ip  SET pr.begin_num = pr.begin_num * ip.valuation_ratio, pr.original_unit_price = pr.original_unit_price * ip.valuation_ratio, pr.rmb_unit_price = pr.rmb_unit_price * ip.valuation_ratio  WHERE \tpr.record_id = '140a9c7c-6778-4b27-88af-40b5b7fd6e52'  \tAND pr.price_record_id = ip.record_id";
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


}
