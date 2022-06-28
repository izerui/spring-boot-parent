package com.yj2025.basic.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.util.concurrent.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.ClearEvent;
import com.yj2025.performance.Consumer;
import com.yj2025.performance.Producer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Context {

    public static ApplicationContext applicationContext;
    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }


    /**
     * 获取spring上下文中的bean对象
     */
    public static <T> T getBean(Class<T> beanClass) {
        return Context.applicationContext.getBean(beanClass);
    }

    /**
     * 触发spring event事件
     */
    public static void dispatchEvent(ApplicationEvent event) {
        Context.applicationContext.publishEvent(event);
    }

    /**
     * 开启手动事务执行
     */
    public static void executeTransaction(java.util.function.Consumer<TransactionStatus> action) {
        TransactionTemplate transactionTemplate = Context.getBean(TransactionTemplate.class);
        transactionTemplate.executeWithoutResult(action);
    }

    /**
     * 开启手动事务执行并返回结果
     */
    public static <T> T executeTransaction(TransactionCallback<T> action) {
        TransactionTemplate transactionTemplate = Context.getBean(TransactionTemplate.class);
        return transactionTemplate.execute(action);
    }


    /**
     * 多线程异步消费发送到队列中的数据,当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass    数据类型
     * @param threadNum 线程数： 建议 5 / 10 / 20 / 30 ...
     * @param consumer  消费者模型
     * @param <T>       发送的数据
     * @return 返回生产者
     */
    public static <T extends ClearEvent> Producer<T> multiConsumer(Class<T> tClass, int threadNum, Consumer<T> consumer) {
        return (Producer<T>) Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(consumer.cloneSelfToMulti(threadNum))
                .requiredRingBufferSize(4096)
                .build();
    }

    /**
     * 多线程异步消费发送到队列中的数据,当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass         数据类型
     * @param threadNum      线程数： 建议 5 / 10 / 20 / 30 ...
     * @param ringBufferSize 环形缓冲区大小，2的幂 ，建议设置合适的值，发送量大的情况下建议大一些，可以使用65536。量小的话建议 4096,数值越大发送效率越高
     * @param consumer       消费者模型
     * @param <T>            发送的数据
     * @return 返回生产者
     */
    public static <T extends ClearEvent> Producer<T> multiConsumer(Class<T> tClass, int threadNum, int ringBufferSize, Consumer<T> consumer) {
        return (Producer<T>) Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(consumer.cloneSelfToMulti(threadNum))
                .requiredRingBufferSize(ringBufferSize)
                .build();
    }


    /**
     * 批量消费发送到队列中的数据, 当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass         发送到队列的数据类型
     * @param maxWaitSeconds 秒数倒计时，当累积数据比较慢，达不到批次数量时，在指定秒内等待数据积累。
     * @param batchLimitSize 批次数量，尽量按当前的批次数进行批操作
     * @param batchConsumer  批量消费者模型， 建议设置批量数量在 500 ~ 3000 范围内。
     * @param <T>            发送的数据
     * @return 返回生产者，
     */
    public static <T extends ClearEvent> Producer<T> batchConsumer(Class<T> tClass, final int maxWaitSeconds, final long batchLimitSize, BatchConsumer<T> batchConsumer) {
        return (Producer<T>) Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(maxWaitSeconds, batchLimitSize, batchConsumer)
                .requiredRingBufferSize(4096)
                .build();
    }

    /**
     * 批量消费发送到队列中的数据, 当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass         发送到队列的数据类型
     * @param maxWaitSeconds 秒数倒计时，当累积数据比较慢，达不到批次数量时，在指定秒内等待数据积累。
     * @param batchLimitSize 批次数量，尽量按当前的批次数进行批操作
     * @param batchConsumer  批量消费者模型， 建议设置批量数量在 500 ~ 3000 范围内。
     * @param ringBufferSize 环形缓冲区大小，2的幂 ，建议设置合适的值，发送量大的情况下建议大一些，可以使用65536。量小的话建议 4096,数值越大发送效率越高
     * @param <T>            发送的数据
     * @return 返回生产者，
     */
    public static <T extends ClearEvent> Producer<T> batchConsumer(Class<T> tClass, final int maxWaitSeconds, final long batchLimitSize, int ringBufferSize, BatchConsumer<T> batchConsumer) {
        return (Producer<T>) Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(maxWaitSeconds, batchLimitSize, batchConsumer)
                .requiredRingBufferSize(ringBufferSize)
                .build();
    }

    /**
     * 提交一批runnable方法到线程池,不等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param runnables       runnable方法集合
     */
    public static void submitAsync(int corePoolSize, int maximumPoolSize, Runnable... runnables) {
        submitAsync(corePoolSize, maximumPoolSize, Arrays.asList(runnables));
    }

    /**
     * 提交一批runnable方法到线程池,不等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param runnables       runnable方法集合
     */
    public static void submitAsync(int corePoolSize, int maximumPoolSize, Collection<Runnable> runnables) {
        submitAsync(corePoolSize, maximumPoolSize, Duration.ZERO, runnables);
    }

    /**
     * 提交一批runnable方法到线程池,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间
     * @param runnables       runnable方法集合
     */
    public static void submitAsyncWait(int corePoolSize, int maximumPoolSize, Duration timeout, Runnable... runnables) {
        submitAsyncWait(corePoolSize, maximumPoolSize, timeout, Arrays.asList(runnables));
    }

    /**
     * 提交一批runnable方法到线程池,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param runnables       runnable方法集合
     */
    public static void submitAsyncWait(int corePoolSize, int maximumPoolSize, Duration timeout, Collection<Runnable> runnables) {
        submitAsync(corePoolSize, maximumPoolSize, timeout, runnables);
    }

    /**
     * 提交一批runnable方法到线程池（注意，异步方法需要手动控制事务）
     */
    private static void submitAsync(int corePoolSize, int maximumPoolSize, Duration timeout, Collection<Runnable> runnables) {
        ListeningExecutorService listeningExecutorService = null;
        ListenableFuture<List<Object>> allAsList = null;
        try {
            listeningExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
            List<ListenableFuture<?>> futures = new ArrayList<>();
            for (Runnable runnable : runnables) {
                ListenableFuture<?> submit = listeningExecutorService.submit(runnable);
                futures.add(submit);
            }
            allAsList = Futures.allAsList(futures);
            if (timeout.toSeconds() != 0L) {
                allAsList.get(timeout.toSeconds(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            if (allAsList != null) {
                allAsList.cancel(true);
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (listeningExecutorService != null) {
                listeningExecutorService.shutdown();
            }
        }
    }


    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,不等待任务全部执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param callback        回调
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    @SafeVarargs
    public static <T> void submitAsync(int corePoolSize, int maximumPoolSize, FutureCallback<T> callback, Callable<T>... callables) {
        submitAsync(corePoolSize, maximumPoolSize, callback, Arrays.asList(callables));
    }

    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,不等待任务全部执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param callback        回调
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    public static <T> void submitAsync(int corePoolSize, int maximumPoolSize, FutureCallback<T> callback, Collection<Callable<T>> callables) {
        submitAsync(corePoolSize, maximumPoolSize, Duration.ZERO, callback, callables);
    }

    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param callback        回调
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    @SafeVarargs
    public static <T> void submitAsyncWait(int corePoolSize, int maximumPoolSize, Duration timeout, FutureCallback<T> callback, Callable<T>... callables) {
        submitAsyncWait(corePoolSize, maximumPoolSize, timeout, callback, Arrays.asList(callables));
    }


    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param callback        回调
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    public static <T> void submitAsyncWait(int corePoolSize, int maximumPoolSize, Duration timeout, FutureCallback<T> callback, Collection<Callable<T>> callables) {
        submitAsync(corePoolSize, maximumPoolSize, timeout, callback, callables);
    }

    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param callback        回调
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    private static <T> void submitAsync(int corePoolSize, int maximumPoolSize, Duration timeout, FutureCallback<T> callback, Collection<Callable<T>> callables) {
        ListeningExecutorService listeningExecutorService = null;
        ListenableFuture<List<T>> allAsList = null;
        try {
            listeningExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
            List<ListenableFuture<T>> futures = new ArrayList<>();
            for (Callable<T> callable : callables) {
                ListenableFuture<T> submit = listeningExecutorService.submit(callable);
                if (callback != null) {
                    Futures.addCallback(submit, callback, listeningExecutorService);
                }
                futures.add(submit);
            }
            allAsList = Futures.allAsList(futures);
            if (timeout.toSeconds() != 0L) {
                allAsList.get(timeout.toSeconds(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            if (allAsList != null) {
                allAsList.cancel(true);
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (listeningExecutorService != null) {
                listeningExecutorService.shutdown();
            }
        }
    }


    /**
     * 创建批量sql（更新、插入）执行器
     *
     * @param dataSource 数据源
     * @param tplSQL     sql
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     * @param consumer   调用 update执行sql
     *                   <code>
     *                   Context.batchExecuteSql(
     *                   dataSource,
     *                   "insert into test_user(version,create_time,code,name,email,age) values (?,?,?,?,?,?)",
     *                   List.of(JDBCType.NUMERIC,
     *                   JDBCType.TIMESTAMP,
     *                   JDBCType.VARCHAR,
     *                   JDBCType.VARCHAR,
     *                   JDBCType.VARCHAR,
     *                   JDBCType.NUMERIC),
     *                   1000,
     *                   batchSqlUpdate -> {
     *                   Arrays.stream(integers).forEach(operand -> {
     *                   batchSqlUpdate.update(0, new Date(), "code" + operand, "张思峰", "mail00" + operand, operand);
     *                   });
     *                   }
     *                   );
     *                   </code>
     */
    public static void batchExecuteSql(DataSource dataSource, String tplSQL, List<JDBCType> parameters, int batchSize, java.util.function.Consumer<BatchSqlUpdate> consumer) {
        BatchSqlUpdate batchSqlUpdate = new BatchSqlUpdate();
        batchSqlUpdate.setDataSource(dataSource);
        batchSqlUpdate.setSql(tplSQL);
        batchSqlUpdate.setBatchSize(batchSize);
        for (JDBCType parameter : parameters) {
            batchSqlUpdate.declareParameter(new SqlParameter(parameter.getVendorTypeNumber()));
        }
        consumer.accept(batchSqlUpdate);
        batchSqlUpdate.flush();
        batchSqlUpdate.reset();
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param dataSource 数据源
     * @param querySQL   查询语句
     * @param pageSize   每页记录数
     * @param rowMapper  行转换器
     * @param <T>
     */
    public static <T> void pagenationQuerySql(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper) {
        pagenationQuerySql(dataSource, querySQL, pageSize, rowMapper, null);
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param dataSource             数据源
     * @param querySQL               查询语句
     * @param pageSize               每页记录数
     * @param rowMapper              行转换器
     * @param perPageResultsConsumer 每页结果合集
     * @param <T>
     */
    public static <T> void pagenationQuerySql(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper, java.util.function.Consumer<List<T>> perPageResultsConsumer) {
        String sql = querySQL + " limit ? offset ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer page = 1;
        while (true) {
            List<T> results = jdbcTemplate.query(sql, rowMapper, pageSize, (page - 1) * pageSize);
            if (results.isEmpty()) {
                break;
            }
            if (perPageResultsConsumer != null) {
                perPageResultsConsumer.accept(results);
            }
            page++;
        }
    }

    /**
     * json序列化
     */
    public static String toJson(Object obj) {
        return wrapExceptions(() -> OBJECT_MAPPER.writeValueAsString(obj));
    }

    /**
     * json反序列化
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return wrapExceptions(() -> OBJECT_MAPPER.readValue(json, tClass));
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常,同时指定message
     */
    public static void wrapExceptions(RunnableWrapper runnable) {
        try {
            runnable.run();
        } catch (java.lang.Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 捕获Exception异常,并且抛出RuntimeException和指定异常message,并返回结果
     */
    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier) {
        try {
            return tSupplier.get();
        } catch (java.lang.Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

//    public static <S, T> T convert(S source, Callable<T> constructor) {
//        T t = wrapExceptions(() -> {
//            T target = constructor.call();
//            return target;
//        });
//        return t;
//    }

    /**
     * 内部类区域
     */

    public interface SupplierWrapper<T> {
        T get() throws java.lang.Exception;
    }

    public interface RunnableWrapper {
        void run() throws java.lang.Exception;
    }

}
