package com.yj2025.basic.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.dadiyang.equator.Equator;
import com.github.dadiyang.equator.FieldInfo;
import com.github.dadiyang.equator.GetterBaseEquator;
import com.google.common.base.CaseFormat;
import com.google.common.util.concurrent.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.ClearEvent;
import com.yj2025.performance.Consumer;
import com.yj2025.performance.Producer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.JDBCType;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public static <T> T getBean(Class<T> beanClass, Class... genericTypes) {
        if (genericTypes == null) {
            return Context.applicationContext.getBean(beanClass);
        } else {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(beanClass, genericTypes);
            ObjectProvider<?> beanProvider = Context.applicationContext.getBeanProvider(resolvableType);
            return (T) beanProvider.getIfAvailable();
        }
    }

    /**
     * 获取一个或者多个beand的相同对象提供者
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> beanClass, Class... genericTypes) {
        if (genericTypes == null) {
            return Context.applicationContext.getBeanProvider(beanClass);
        } else {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(beanClass, genericTypes);
            return Context.applicationContext.getBeanProvider(resolvableType);
        }

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
                .requiredRingBufferSize(1024 * 64)
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
                .requiredRingBufferSize(1024 * 64)
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
     * 创建批量sql（更新、插入）执行器, 数据update执行完毕后，调用flush、最好也调用reset。
     *
     * @param placeSQL   占位符SQL
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     */
    public static BatchSqlUpdate batchUpdate(String placeSQL, List<JDBCType> parameters, int batchSize) {
        return batchUpdate(getBean(DataSource.class), placeSQL, parameters, batchSize);
    }

    /**
     * 创建批量sql（更新、插入）执行器, 数据update执行完毕后，调用flush、最好也调用reset。
     *
     * @param dataSource 数据源
     * @param placeSQL   占位符SQL
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     */
    public static BatchSqlUpdate batchUpdate(DataSource dataSource, String placeSQL, List<JDBCType> parameters, int batchSize) {
        BatchSqlUpdate batchSqlUpdate = new BatchSqlUpdate();
        batchSqlUpdate.setDataSource(dataSource);
        batchSqlUpdate.setSql(placeSQL);
        batchSqlUpdate.setBatchSize(batchSize
        );
        for (JDBCType parameter : parameters) {
            batchSqlUpdate.declareParameter(new SqlParameter(parameter.getVendorTypeNumber()));
        }
        return batchSqlUpdate;
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static int[] batchUpdate(String namedSQL, Map<String, ?>[] batchValues) {
        return batchUpdate(getBean(DataSource.class), namedSQL, batchValues);
    }

    private final static Map<DataSource, NamedParameterJdbcTemplate> NAMED_PARAMETER_JDBCTEMPLATE_MAP = new HashMap<>();

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param dataSource  数据源
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static int[] batchUpdate(DataSource dataSource, String namedSQL, Map<String, ?>[] batchValues) {
        if (batchValues == null || batchValues.length == 0) {
            return new int[0];
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = NAMED_PARAMETER_JDBCTEMPLATE_MAP.get(dataSource);
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            NAMED_PARAMETER_JDBCTEMPLATE_MAP.put(dataSource, namedParameterJdbcTemplate);
        }
        return namedParameterJdbcTemplate.batchUpdate(namedSQL, batchValues);
    }

    /**
     * 将对象转换成map,浅拷贝
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Map<String, ?> mapOfOriginKey(T data) {
        if (data instanceof Map) {
            return (Map<String, ?>) data;
        } else {
            Map<String, Object> map = new HashMap<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(data.getClass());
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null) {
                    String originName = pd.getName();
//                        String camelName = pd.getName();
//                        String underscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), data);
//                        map.put(camelName, value);
                    map.put(originName, value);
                }
            }
            return map;
        }
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static <T> int[] batchUpdate(String namedSQL, Collection<T> batchValues) {
        return batchUpdate(getBean(DataSource.class), namedSQL, batchValues);
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param dataSource  数据源
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static <T> int[] batchUpdate(DataSource dataSource, String namedSQL, Collection<T> batchValues) {
        if (batchValues == null || batchValues.isEmpty()) {
            return new int[0];
        }
        AtomicInteger it = new AtomicInteger(0);
        Map<String, ?>[] batchMaps = new HashMap[batchValues.size()];
        for (Object batchValue : batchValues) {
            batchMaps[it.getAndIncrement()] = mapOfOriginKey(batchValue);
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = NAMED_PARAMETER_JDBCTEMPLATE_MAP.get(dataSource);
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            NAMED_PARAMETER_JDBCTEMPLATE_MAP.put(dataSource, namedParameterJdbcTemplate);
        }
        return namedParameterJdbcTemplate.batchUpdate(namedSQL, batchMaps);
    }

    /**
     * 批量异步执行更新语句
     *
     * @param updateSQL  更新语句
     * @param primaryKey 按批次根据主键更新
     * @param poolSize   执行线程个数
     * @param batchNum   每批次数量
     */
    public static void batchUpdateAsync(String updateSQL, String primaryKey, int poolSize, int batchNum) {
        batchUpdateAsync(Context.getBean(DataSource.class), updateSQL, primaryKey, poolSize, batchNum, null);
    }

    /**
     * 批量异步执行更新语句
     *
     * @param updateSQL     更新语句
     * @param primaryKey    按批次根据主键更新
     * @param poolSize      执行线程个数
     * @param batchNum      每批次数量
     * @param batchConsumer 每批次观察
     */
    public static void batchUpdateAsync(String updateSQL, String primaryKey, int poolSize, int batchNum, BiConsumer<List<?>, Integer> batchConsumer) {
        batchUpdateAsync(Context.getBean(DataSource.class), updateSQL, primaryKey, poolSize, batchNum, batchConsumer);
    }

    /**
     * 批量异步执行更新语句
     *
     * @param dataSource    数据源
     * @param updateSQL     更新语句
     * @param primaryKey    按批次根据主键更新
     * @param poolSize      执行线程个数
     * @param batchNum      每批次数量
     * @param batchConsumer 每批次观察
     */
    public static void batchUpdateAsync(DataSource dataSource, String updateSQL, String primaryKey, int poolSize, int batchNum, BiConsumer<List<?>, Integer> batchConsumer) {
        SqlParser sqlParser = SqlParser.create(updateSQL);
        SqlUpdate sqlUpdate = (SqlUpdate) Context.tryWith(() -> sqlParser.parseStmt());
        String tablename = sqlUpdate.getTargetTable().toString();
        String updateWithoutCondition = sqlUpdate.toString().split("\n")[0];
        String condition = sqlUpdate.getCondition().toString();
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
        List<ListenableFuture<?>> futures = new ArrayList<>();
        Context.pagenationQueryWrap(Context.getBean(DataSource.class), "select " + primaryKey + " from " + tablename + " where " + condition, batchNum,
                (rs, rowNum) -> rs.getObject(primaryKey),
                (primaryValues, page) -> {
                    ListenableFuture<?> future = executorService.submit(() -> {
                        String usql = updateWithoutCondition + " where " + primaryKey + " = :key";
                        List<HashMap> maps = primaryValues.stream().map(primaryValue -> new HashMap(1) {{
                            put("key", primaryValue);
                        }}).collect(Collectors.toList());
                        Context.batchUpdate(dataSource, usql, maps);
                        if (batchConsumer != null) {
                            batchConsumer.accept(primaryValues, page);
                        }
                    });
                    futures.add(future);
                });
        Context.tryWith(() -> Futures.allAsList(futures).get());
        executorService.shutdown();
    }

    /**
     * 全局静态表结构 SimpleJdbcInsert，高速缓存
     */
    private final static Map<String, SimpleJdbcInsert> GLOB_TABLE_INSERT_HOLDER = new ConcurrentHashMap<>();

    private static SimpleJdbcInsert getInsert(DataSource dataSource, String tablename, String... generatedKeys) {
        String mapKey = tablename + String.join("_", generatedKeys);
        SimpleJdbcInsert insert = GLOB_TABLE_INSERT_HOLDER.get(mapKey);
        if (insert == null) {
            insert = new SimpleJdbcInsert(dataSource);
            insert.setTableName(tablename);
            if (generatedKeys != null && generatedKeys.length > 0) {
                insert.usingGeneratedKeyColumns(generatedKeys);
            }
            insert.compile();
            GLOB_TABLE_INSERT_HOLDER.put(mapKey, insert);
        }
        return insert;
    }

    /**
     * 将对象转换成map，key由驼峰转换成下划线(浅拷贝)
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Map<String, ?> mapOfUnderscoreKey(T data) {
        if (data instanceof Map) {
            return (Map<String, ?>) data;
        } else {
            Map<String, Object> map = new HashMap<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(data.getClass());
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null) {
                    String underscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), data);
                    map.put(underscoreName, value);
                }
            }
            return map;
        }
    }

    /**
     * 新增一条记录到数据库表，并返回主键值
     *
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> Number insertReturnKey(String tablename, T data, String generatedKeys) {
        return insertReturnKey(getBean(DataSource.class), tablename, data, generatedKeys);
    }

    /**
     * 新增一条记录到数据库表，并返回主键值
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> Number insertReturnKey(DataSource dataSource, String tablename, T data, String generatedKeys) {
        Assert.notNull(generatedKeys, "必须指定主键才能获取返回的主键值");
        return getInsert(dataSource, tablename, generatedKeys)
                .executeAndReturnKey(mapOfUnderscoreKey(data));
    }

    /**
     * 新增一条记录到数据库表
     *
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> int insert(String tablename, T data, String... generatedKeys) {
        return insert(getBean(DataSource.class), tablename, data, generatedKeys);
    }

    /**
     * 新增一条记录到数据库表
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> int insert(DataSource dataSource, String tablename, T data, String... generatedKeys) {
        return getInsert(dataSource, tablename, generatedKeys)
                .execute(mapOfUnderscoreKey(data));
    }

    /**
     * 批量插入数据库
     *
     * @param tablename     表名
     * @param batchValues   批次数据
     * @param generatedKeys 自动生成的key
     * @param <T>
     */
    public static <T> int[] batchInsert(String tablename, Collection<T> batchValues, String... generatedKeys) {
        return batchInsert(getBean(DataSource.class), tablename, batchValues, generatedKeys);
    }


    /**
     * 批量插入数据库
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param batchValues   批次数据
     * @param generatedKeys 自动生成的key
     * @param <T>
     */
    public static <T> int[] batchInsert(DataSource dataSource, String tablename, Collection<T> batchValues, String... generatedKeys) {
        if (batchValues == null || batchValues.isEmpty()) {
            return new int[0];
        }
        AtomicInteger it = new AtomicInteger(0);
        Map<String, ?>[] batchMaps = new HashMap[batchValues.size()];
        for (T batchValue : batchValues) {
            batchMaps[it.getAndIncrement()] = mapOfUnderscoreKey(batchValue);
        }
        return getInsert(dataSource, tablename, generatedKeys).executeBatch(batchMaps);
    }

    /**
     * 将查询语句分批次查询，直到所有满足条件的数据都查询完
     *
     * @param querySQL  查询语句
     * @param pageSize  每页记录数
     * @param rowMapper 行转换器
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(String querySQL, int pageSize, RowMapper<T> rowMapper) {
        pagenationQueryWrap(getBean(DataSource.class), querySQL, pageSize, rowMapper);
    }


    /**
     * 将查询语句分批次查询，直到所有满足条件的数据都查询完
     *
     * @param dataSource 数据源
     * @param querySQL   查询语句
     * @param pageSize   每页记录数
     * @param rowMapper  行转换器
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper) {
        pagenationQueryWrap(querySQL, pageSize, rowMapper, null);
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param querySQL      查询语句
     * @param pageSize      每页记录数
     * @param rowMapper     行转换器
     * @param batchConsumer 每批观察
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(String querySQL, int pageSize, RowMapper<T> rowMapper, BiConsumer<List<T>, Integer> batchConsumer) {
        pagenationQueryWrap(getBean(DataSource.class), querySQL, pageSize, rowMapper, batchConsumer);
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param dataSource    数据源
     * @param querySQL      查询语句
     * @param pageSize      每页记录数
     * @param rowMapper     行转换器
     * @param batchConsumer 每批观察
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper, BiConsumer<List<T>, Integer> batchConsumer) {
        String sql = querySQL + " limit ? offset ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer page = 1;
        while (true) {
            List<T> results = jdbcTemplate.query(sql, rowMapper, pageSize, (page - 1) * pageSize);
            if (results.isEmpty()) {
                break;
            }
            if (batchConsumer != null) {
                batchConsumer.accept(results, page);
            }
            page++;
        }
    }

    /**
     * 获取两个对象的属性的区别
     *
     * @param first
     * @param second
     * @param <T>
     * @return
     */
    public static <T extends Object> List<FieldInfo> diff(T first, T second) {
        Equator equator = new GetterBaseEquator();
        return equator.getDiffFields(first, second);
    }

    /**
     * 修改excel文件，针对单元格增加下来校验
     *
     * @param source   源文件
     * @param target   目标文件
     * @param list     下拉内容
     * @param firstRow 起始行
     * @param lastRow  结束行
     * @param firstCol 起始列
     * @param lastCol  结束列
     */
    public static void readerXlsDropdown(File source, File target, List<String> list, int firstRow, int lastRow, int firstCol, int lastCol) {
        try (XSSFWorkbook wb = new XSSFWorkbook(source);
             FileOutputStream out = new FileOutputStream(target)) {
            // 选中指定sheet
            XSSFSheet sheet = wb.getSheetAt(0);
            String[] values = list.toArray(new String[list.size()]);

            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(values);
            CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
            //这两行设置单元格只能是列表中的内容，否则报错
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
            // 写入文件
            wb.write(out);
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json序列化
     */
    public static String toJson(Object obj) {
        return tryWith(() -> OBJECT_MAPPER.writeValueAsString(obj));
    }

    /**
     * json反序列化
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return tryWith(() -> OBJECT_MAPPER.readValue(json, tClass));
    }

    /**
     * json反序列化
     */
    public static <T> T fromJson(byte[] json, Class<T> tClass) {
        return tryWith(() -> OBJECT_MAPPER.readValue(json, tClass));
    }

    /**
     * map转成另一个map
     *
     * @param originMap   原始map
     * @param keyMapper   k转换器
     * @param valueMapper v转换器
     * @return 新的map
     */
    public static <K, V, T, U> Map<T, U> mapToMap(Map<K, V> originMap, Function<Map.Entry<K, V>, ? extends T> keyMapper,
                                                  Function<Map.Entry<K, V>, ? extends U> valueMapper) {
        return originMap.entrySet()
                .stream()
                .collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 捕获Exception异常,SneakyThrows
     */
    public static void tryWith(CheckedRunnable runnable) {
        Try.run(runnable).get();
    }

    /**
     * 捕获Exception异常,SneakyThrows,并返回结果
     */
    public static <T> T tryWith(CheckedFunction0<T> tSupplier) {
        return Try.of(tSupplier).get();
    }

}
