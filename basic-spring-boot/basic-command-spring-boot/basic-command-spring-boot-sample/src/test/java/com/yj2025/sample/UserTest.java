package com.yj2025.sample;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.*;
import com.yj2025.basic.support.Context;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.ClearEvent;
import com.yj2025.performance.Producer;
import com.yj2025.sample.service.ConditionEntity;
import com.yj2025.sample.service.UpdateBatchExecutor;
import com.yj2025.sample.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class)
@Transactional
@Rollback(value = false)
public class UserTest {

    @Autowired
    private DataSource dataSource;

    @SpyBean
    private UserService userService;

    @Before
    public void init() throws IOException {
        BDDMockito.willReturn("测试用户").given(userService).getWrapHeader().getUserName();
    }


    @Test
    public void testAdd() {
        userService.add();
    }

    @Test
    public void testBatchAdd() {
        userService.batchAdd();
    }

    @Test
    public void testBatchAdd2() {
        userService.batchAdd2();
    }

    @Test
    public void testBatchAdd3() {
        userService.batchAdd3();
    }

    @Test
    public void testBatchAdd4() {
        userService.batchAdd4();
    }

    @Test
    public void testBatchAdd5() {
        userService.batchAdd5();
    }

    @Test
    public void testQueryPage() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Context.pagenationQueryWrap("select id from test_user", 5000, (rs, rowNum) -> {
//            System.out.println(rs.getLong("id"));
            return null;
        });
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testContextPagenationUpdate() {
        BatchSqlUpdate batchSqlUpdate = Context.batchUpdate(dataSource, "update test_user set age = 18 where id = ?", List.of(JDBCType.NUMERIC), 5000);
        Context.pagenationQueryWrap(dataSource, "select id from test_user", 5000,
                (rs, rowNum) -> rs.getLong("id"),
                (ids, page) -> {
                    log.debug("第{}页 当前处理数量: {}", page, ids.size());
                    for (Long id : ids) {
                        batchSqlUpdate.update(id);
                    }
                });
        batchSqlUpdate.flush();
        batchSqlUpdate.reset();
    }

    @Test
    public void testContextPagenationUpdate2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Context.pagenationQueryWrap(dataSource, "select id from test_user", 5000,
                (rs, rowNum) -> rs.getLong("id"),
                (ids, page) -> {
                    log.debug("第{}页 当前处理数量: {}", page, ids.size());
                    List<HashMap> maps = ids.stream().map(aLong -> new HashMap(1) {{
                        put("id", aLong);
                    }}).collect(Collectors.toList());
                    Context.batchUpdate("update test_user set age = 18 where id = :id", maps);
                });
        System.out.println("耗时：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testContextPagenationUpdate3() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
        List<ListenableFuture<?>> futures = new ArrayList<>();
        Context.pagenationQueryWrap(dataSource, "select id from test_user", 5000,
                (rs, rowNum) -> rs.getLong("id"),
                (ids, page) -> {
                    ListenableFuture<?> submit = executorService.submit(() -> {
                        log.debug("第{}页 当前处理数量: {}", page, ids.size());
                        List<HashMap> maps = ids.stream().map(aLong -> new HashMap(1) {{
                            put("id", aLong);
                        }}).collect(Collectors.toList());
                        Context.batchUpdate("update test_user set age = 18 where id = :id", maps);
                    });
                    futures.add(submit);
                });
        Futures.allAsList(futures).get();
        executorService.shutdown();
        System.out.println("耗时：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testContextPagenationUpdate4() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 分批按主键ID更新
//        Context.batchUpdateAsync("update test_user set age = 18", "id", 5, 5000);
        // 分批按主键ID更新并且 监控已更新数据
        Context.batchUpdateAsync("update test_user set age = 18 where age > 16 and code is not null", "id", 5, 5000, (ids, page) -> {
//            log.debug("第{}页 当前处理数量: {}", page, ids.size());
            System.out.println(page);
        });
        System.out.println("耗时：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testUpdate() {
        new JdbcTemplate(dataSource).execute("update test_user set age = 18");
    }

    @Test
    public void testBatchUpdate() {
        long a = System.currentTimeMillis();
        List<Long> longs = new ArrayList<>();
//        IntStream.range(0, 10).forEach(value -> {
        UpdateBatchExecutor batchExecutor = new UpdateBatchExecutor(dataSource, 10, 10);
        long begin = System.currentTimeMillis();
        ConditionEntity conditionEntity = new ConditionEntity();
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 20);
        batchExecutor.execute("test_user", conditionEntity.where("1=1"), updates);
        longs.add(System.currentTimeMillis() - begin);
//        });
        Long value = 0L;
        for (Long aLong : longs) {
            value = value + aLong;
        }
        System.out.println("平均耗时：" + value / 10);
        System.out.println("总耗时：" + (System.currentTimeMillis() - a));
    }

    @Data
    public static class PageId implements ClearEvent {
        private Integer page;
        private Integer id;

        @Override
        public void clear() {
            this.page = null;
            this.id = null;
        }
    }

    @Test
    public void testBatchUpdate2() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        long a = System.currentTimeMillis();
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 10);
        Producer<PageId> producer = Context.batchConsumer(PageId.class, 1, 1000, new BatchConsumer<PageId>() {
            @Override
            protected void handlerEvent(List<PageId> correlationData, long sequence) {
                if (correlationData.isEmpty()) {
                    return;
                }
                List<Integer> collect = correlationData.stream().map(PageId::getId).collect(Collectors.toList());
                executorService.execute(() -> {
                    batchUpdateSQL("test_user", collect, updates);
                });
            }
        });

        IntStream.range(0, 10).forEach(value -> {
            batchGetPrimaryIds("test_user", new ConditionEntity().where("1=1"), (page, id) -> {
                try {
                    producer.sendData(pageId -> {
                        pageId.setPage(page);
                        pageId.setId(id);
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            });
        });
        System.out.println("总耗时：" + (System.currentTimeMillis() - a));
    }

    /**
     * 组装update 语句的set 内容
     *
     * @param updates
     * @return
     */
    private String assemblySql(Map<String, Object> updates) {
        return updates.keySet().stream().map(column -> {
            Object obj = updates.get(column);
            if (obj == null) {
                return column + " = null ";
            } else if (obj instanceof Boolean) {
                return column + " = " + ((Boolean) obj ? "true" : "false");
            } else if (obj instanceof Integer || obj instanceof Double || obj instanceof Float) {
                return column + " = " + obj;
            }
            return column + " = '" + obj.toString().replaceAll("'", "\\\\'") + "'";
        }).collect(Collectors.joining(","));
    }

    private Void batchUpdateSQL(String tableName, List<Integer> ids, Map<String, Object> updates) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        try {
            log.info("批量更新 表名:{}  记录数:{}", tableName, ids.size());
            connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
            String sql = "update " + tableName + " set " + assemblySql(updates) + " where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            for (Integer id : ids) {
                preparedStatement.setObject(1, id);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            connection.commit();
            preparedStatement.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return null;
    }

    private void batchGetPrimaryIds(String tableName, ConditionEntity conditionEntity, BiConsumer<Integer, Integer> consumer) {
        Assert.notNull(conditionEntity);
        String whereQL = conditionEntity.build();
        String sql = "select id from " + tableName + whereQL + " limit ? offset ?";
        Integer page = 1;
        Connection connection = null;
        PreparedStatement preparedStatement;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            preparedStatement = connection.prepareStatement(sql);
            while (true) {
                preparedStatement.setObject(1, 1000);
                preparedStatement.setObject(2, (page - 1) * 1000);
                boolean hasData = false;
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    hasData = true;
                    Integer id = resultSet.getInt(1);
                    consumer.accept(page, id);
                }
                resultSet.close();
                if (!hasData) {
                    break;
                }
                log.info("批量查询 表名:{} 页数:{}  匹配条数:{} 语句 {} ", tableName, page, null, sql);
                page++;
            }
            preparedStatement.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Test
    public void testAsyncRun() {
        Context.submitAsyncWait(3, 5, Duration.ofSeconds(10), this::testBatchUpdate, this::testBatchUpdate2);
        System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
    }

    @Test
    public void testCallback() throws InterruptedException {
        Callable<String>[] callables = new Callable[20000];
        for (int i = 0; i < 20000; i++) {
            int finalI = i;
            callables[i] = () -> {
//                Thread.sleep(RandomUtils.nextInt(50, 300));
                return "---" + finalI;
            };
        }
        CountDownLatch countDownLatch = new CountDownLatch(20000);
        List<String> strings = Context.submitAsyncWaitReturn(3, 5, Duration.ofSeconds(60), callables);
        System.out.println(strings);
    }

}
