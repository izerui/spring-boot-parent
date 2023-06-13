package com.yj2025.basic.support;

import com.google.common.base.CaseFormat;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

import javax.persistence.Column;
import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DbContext {

    /**
     * 开启手动事务执行
     */
    public static void executeTransaction(Consumer<TransactionStatus> action) {
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
     * 创建批量sql（更新、插入）执行器, 数据update执行完毕后，调用flush、最好也调用reset。
     *
     * @param placeSQL   占位符SQL
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     */
    public static BatchSqlUpdate batchUpdate(String placeSQL, List<JDBCType> parameters, int batchSize) {
        return batchUpdate(Context.getBean(DataSource.class), placeSQL, parameters, batchSize);
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
        return batchUpdate(Context.getBean(DataSource.class), namedSQL, batchValues);
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
        return batchUpdate(Context.getBean(DataSource.class), namedSQL, batchValues);
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
        pagenationQueryWrap(Context.getBean(DataSource.class), "select " + primaryKey + " from " + tablename + " where " + condition, batchNum,
                (rs, rowNum) -> rs.getObject(primaryKey),
                (primaryValues, page) -> {
                    ListenableFuture<?> future = executorService.submit(() -> {
                        String usql = updateWithoutCondition + " where " + primaryKey + " = :key";
                        List<HashMap> maps = primaryValues.stream().map(primaryValue -> new HashMap(1) {{
                            put("key", primaryValue);
                        }}).collect(Collectors.toList());
                        batchUpdate(dataSource, usql, maps);
                        if (batchConsumer != null) {
                            batchConsumer.accept(primaryValues, page);
                        }
                    });
                    futures.add(future);
                });
        try {
            Futures.allAsList(futures).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                String dbFieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                boolean isToJson = false;
                Field field = ReflectionUtils.findField(data.getClass(), pd.getName());
                if (field != null) {
                    // 更改字段名
                    Column columnDef = field.getAnnotation(Column.class);
                    if (columnDef != null && StringUtils.isNotBlank(columnDef.name())) {
                        dbFieldName = columnDef.name();
                    }
                    // 是否是json字段类型
                    Type annotation = field.getAnnotation(Type.class);
                    isToJson = annotation != null;
                }
                if (pd.getReadMethod() != null) {
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), data);
                    if (isToJson) {
                        map.put(dbFieldName, Context.toJson(value));
                    } else {
                        map.put(dbFieldName, value);
                    }

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
    public static <T> Number insertReturnKey(String tablename, T data, String... generatedKeys) {
        return insertReturnKey(Context.getBean(DataSource.class), tablename, data, generatedKeys);
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
    public static <T> Number insertReturnKey(DataSource dataSource, String tablename, T data, String... generatedKeys) {
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
        return insert(Context.getBean(DataSource.class), tablename, data, generatedKeys);
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
        return batchInsert(Context.getBean(DataSource.class), tablename, batchValues, generatedKeys);
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
        pagenationQueryWrap(Context.getBean(DataSource.class), querySQL, pageSize, rowMapper);
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
        pagenationQueryWrap(Context.getBean(DataSource.class), querySQL, pageSize, rowMapper, batchConsumer);
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
     * 查询返回指定的分页结果
     *
     * @param dataSource
     * @param querySQL
     * @param page
     * @param pageSize
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> paginationQuery(DataSource dataSource, String querySQL, int page, int pageSize, Class<T> tClass) {
        int offset = (page - 1) * pageSize;
        if (offset < 0) {
            offset = 0;
        }
        String sql = querySQL + " limit ? offset ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        BeanPropertyRowMapper<T> beanPropertyRowMapper = new BeanPropertyRowMapper<>();
        beanPropertyRowMapper.setMappedClass(tClass);
        List<T> list = jdbcTemplate.query(sql, beanPropertyRowMapper, pageSize, offset);
        return list;
    }

    public static <T> Page<T> paginationQuery(DataSource dataSource, String querySQL, Pageable pageable, Map<String, Object> params, Class<T> tClass) {
        String sql = "SELECT * " + querySQL + getSortSqlAndInitParams(pageable) + " limit :pageSize offset :offset";
        String countSQL = "SELECT COUNT(0) " + querySQL;

        params.put("pageSize", pageable.getPageSize());
        params.put("offset", pageable.getOffset());

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        BeanPropertyRowMapper<T> beanPropertyRowMapper = new BeanPropertyRowMapper<>();
        beanPropertyRowMapper.setMappedClass(tClass);
        return new PageImpl<T>(
                jdbcTemplate.query(sql, params, beanPropertyRowMapper),
                pageable,
                jdbcTemplate.queryForObject(countSQL, params, Long.class));
    }

    public static <T> List<T> findAll(DataSource dataSource, String querySQL, Map<String, Object> params, Class<T> tClass) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        BeanPropertyRowMapper<T> beanPropertyRowMapper = new BeanPropertyRowMapper<>();
        beanPropertyRowMapper.setMappedClass(tClass);
        return jdbcTemplate.query(querySQL, params, beanPropertyRowMapper);
    }

    private static String getSortSqlAndInitParams(Pageable pageable) {
        return " ORDER BY " + camel2Sql(pageable.getSort().toString()).replaceAll(":", "");
    }

    private static String camel2Sql(String sql) {
        return sql.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

}
