package junit;

import com.yj2025.jpa.impl.Conditions;
import com.yj2025.jpa.impl.ConditionsAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.query.Criteria;

public class TestJdbc {

    @Test
    public void test01() {
        Conditions where = Conditions.where("recordStatus=1 and title = 1 or (nameAge=2 and ttt = 3)");
        System.out.println("jpql: " + where);
        Criteria criteria = new ConditionsAdapter(where).toCriteria(ConditionsAdapter.camelToUnderscore);
        System.out.println("sql: " + criteria);
    }

    @Test
    public void test02() {
        Conditions conditions = Conditions.where("A").is(1)
                .and("B").is(2)
                .or(
                        Conditions.where("C").like("%222%")
                )
                .and(
                        Conditions.where("E").is(5).or("F").is(6)
                );
        conditions.and("G").is("sjsj").and("H").like("%ffff%");

        System.out.println("jpql: " + conditions);

        // 顺手测试下 转jdbc的连接器
        ConditionsAdapter adapter = new ConditionsAdapter(conditions);
        org.springframework.data.relational.core.query.Criteria criteria = adapter.toCriteria(null);
        System.out.println("sql: " + criteria);
    }
}
