package com.yj2025.sample.service;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 建议定义一个全局单例来使用,例如注册为spring bean
 */
@Slf4j
public class UpdateBatchExecutor {

    private DataSource dataSource;
    private ListeningExecutorService executorService;

    public UpdateBatchExecutor(DataSource dataSource) {
        this(dataSource, 5, 10);
    }

    public UpdateBatchExecutor(DataSource dataSource, int corePoolSize, int maximumPoolSize) {
        this.dataSource = dataSource;
        this.executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
    }

    public void execute(String tableName, ConditionEntity conditionEntity, Map<String, Object> updates) {
        List<Callable<Void>> callables = new ArrayList<>();
        this.batchGetPrimaryIds(tableName, conditionEntity, (page, ids) -> {
            callables.add(() -> this.batchUpdateSQL(tableName, page, ids, updates));
        });
        this.executeReturn(callables);
    }

    private <T> List<T> executeReturn(List<Callable<T>> callables) {
        List<ListenableFuture<T>> futures = new ArrayList<>();
        for (Callable<T> callable : callables) {
            ListenableFuture<T> listenableFuture = executorService.submit(callable);
            futures.add(listenableFuture);
        }
        ListenableFuture<List<T>> allAsList = Futures.allAsList(futures);
        List<T> list;
        try {
            list = allAsList.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return list;
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

    private Void batchUpdateSQL(String tableName, Integer page, List<Integer> ids, Map<String, Object> updates) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        try {
            log.info("批量更新 表名:{}  页数:{}  记录数:{}", tableName, page, ids.size());
            connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
            String sql = "update " + tableName + " set " + assemblySql(updates) + " where id = ?";
            log.info("语句:{} ids{}", sql, ids);
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

    private void batchGetPrimaryIds(String tableName, ConditionEntity conditionEntity, BiConsumer<Integer, List<Integer>> consumer) {
        Assert.notNull(conditionEntity, "conditionEntity must not be null");
        String whereQL = conditionEntity.build();
        String sql = "select id from " + tableName + whereQL + " limit ? offset ?";
        Integer page = 1;
        Connection connection = null;
        PreparedStatement preparedStatement;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            preparedStatement = connection.prepareStatement(sql);
            while (true) {
                preparedStatement.setObject(1, 5000);
                preparedStatement.setObject(2, (page - 1) * 5000);
                ResultSet resultSet = preparedStatement.executeQuery();
                List<Integer> ids = new ArrayList<>();
                while (resultSet.next()) {
                    ids.add(resultSet.getInt(1));
                }
                resultSet.close();
                if (ids.isEmpty()) {
                    break;
                }
                log.info("批量查询 表名:{} 页数:{}  匹配条数:{} 语句 {} ", tableName, page, ids.size(), sql);
                consumer.accept(page, ids);
                page++;
            }
            preparedStatement.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
