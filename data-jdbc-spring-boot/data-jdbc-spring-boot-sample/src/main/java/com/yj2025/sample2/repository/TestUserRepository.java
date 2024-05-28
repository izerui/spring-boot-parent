package com.yj2025.sample2.repository;

import com.yj2025.jdbc.PlatformJdbcRepository;
import com.yj2025.jdbc.dialect.flag.QueryFlagAfterTable;
import com.yj2025.sample2.entity.TestUser;
import com.yj2025.tenant.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TestUserRepository extends PlatformJdbcRepository<TestUser, Long> {

    @Query("select * from #{@sharding.getTable('test_user')} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);

    @Query("select * from #{@sharding.getTable('test_user')} where code in (:codes)")
    List<TestUser> findList(String entCode, List<String> codes);


    @Tenant("#{#entCode}")
    @QueryFlagAfterTable("query标注: #{#code}")
    List<TestUser> findByCode(String entCode, String code);

    @Tenant("#{#map['ent_code']}")
    @Override
    @QueryFlagAfterTable("query标注: #{#code}")
    List<TestUser> findAll(Map<String, Object> map);

    List<TestUser> findByEntCodeAndAccountingPeriod(String  entCode, YearMonth accountingPeriod);

    List<TestUser> findByEntCodeAndAccountingPeriodIn(String entCode, Collection<YearMonth> accountingPeriods);

    /**
     * 优先匹配有值的tablePrefix前缀,最后匹配空串,发现空串算匹配成功,则添加表后缀字符串
     * 第一种写法:
     */
//    @QueryFlagAfterTables(
//            {
//                    @QueryFlagAfterTable(value = "force index(idx_01)", isComment = false, tablePrefix = "test_user_ent001"),
//                    @QueryFlagAfterTable(value = "force index(idx_02)", isComment = false, tablePrefix = "test_user_ent002")
//            }
//    )

    /**
     * 优先匹配有值的tablePrefix前缀,最后匹配空串,发现空串算匹配成功,则添加表后缀字符串
     * 第二种写法
     */
    @QueryFlagAfterTable(value = "force index(idx_01)", isComment = false)
    @QueryFlagAfterTable(value = "force index(idx_02)", isComment = false, tablePrefix = "test_user_ent002")
    default Page<TestUser> findAllForceIndex(org.springframework.data.relational.core.query.Query query, Pageable pageable) {
        System.out.println("第二步：切面放入本地线程queryflag值，并运用到业务逻辑中");
        return this.findAll(query, pageable);
//        System.out.println("第三步：切面即将跳出，即将清空本地线程的queryflag值");
    }

}
