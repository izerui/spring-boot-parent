package com.yj2025.basic.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.dadiyang.equator.Equator;
import com.github.dadiyang.equator.FieldInfo;
import com.github.dadiyang.equator.GetterBaseEquator;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.lmax.disruptor.dsl.ProducerType;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.ClearEvent;
import com.yj2025.performance.Producer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
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
     * 多线程异步消费发送到队列中的数据,当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass    数据类型
     * @param threadNum 线程数： 建议 5 / 10 / 20 / 30 ...
     * @param consumer  消费者模型
     * @param <T>       发送的数据
     * @return 返回生产者
     */
    public static <T extends ClearEvent> Producer<T> multiConsumer(Class<T> tClass, int threadNum, com.yj2025.performance.Consumer<T> consumer) {
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
    public static <T extends ClearEvent> Producer<T> multiConsumer(Class<T> tClass, int threadNum, int ringBufferSize, com.yj2025.performance.Consumer<T> consumer) {
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
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy());
            for (Runnable runnable : runnables) {
                threadPoolExecutor.submit(runnable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
        }
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
     * @param timeout         每个线程超时时间
     * @param runnables       runnable方法集合
     */
    public static void submitAsyncWait(int corePoolSize, int maximumPoolSize, Duration timeout, Collection<Runnable> runnables) {
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy());
            List<Future<?>> futures = new ArrayList<>();
            for (Runnable runnable : runnables) {
                Future<?> submit = threadPoolExecutor.submit(runnable);
                futures.add(submit);
            }
            futures.forEach(future -> tryWith(() -> {
                if (timeout.toSeconds() != 0L) {
                    future.get(timeout.toSeconds(), TimeUnit.SECONDS);
                } else {
                    future.get();
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
        }
    }


    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    @SafeVarargs
    public static <T> List<T> submitAsyncWaitReturn(int corePoolSize, int maximumPoolSize, Duration timeout, Callable<T>... callables) {
        return submitAsyncWaitReturn(corePoolSize, maximumPoolSize, timeout, Arrays.asList(callables));
    }


    /**
     * 提交一批callable方法到线程池,每个处理完返调用callback,并等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param timeout         每个线程超时时间 0:永不超时
     * @param callables       callable方法集合
     * @param <T>             返回值类型
     */
    public static <T> List<T> submitAsyncWaitReturn(int corePoolSize, int maximumPoolSize, Duration timeout, Collection<Callable<T>> callables) {
        ListeningExecutorService listeningExecutorService = null;
        ListenableFuture<List<T>> allAsList = null;
        try {
            listeningExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy()));
            List<ListenableFuture<T>> futures = new ArrayList<>();
            for (Callable<T> callable : callables) {
                ListenableFuture<T> submit = listeningExecutorService.submit(callable);
                futures.add(submit);
            }
            allAsList = Futures.allAsList(futures);
            if (timeout.toSeconds() != 0L) {
                return allAsList.get(timeout.toSeconds(), TimeUnit.SECONDS);
            } else {
                return allAsList.get();
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
     * json反序列化
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        return tryWith(() -> OBJECT_MAPPER.readValue(json, valueTypeRef));
    }

    /**
     * json反序列化
     */
    public static <T> T fromJson(byte[] json, TypeReference<T> valueTypeRef) {
        return tryWith(() -> OBJECT_MAPPER.readValue(json, valueTypeRef));
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
     * list转成map
     *
     * @param iterable    列表数据
     * @param keyMapper   key转换器
     * @param valueMapper value转换器
     * @param <K>         map的key类型
     * @param <V>         map的value类型
     * @param <T>         列表数据对象类型
     * @return map
     */
    public static <K, V, T> Map<K, V> listToMap(Iterable<T> iterable, Function<? super T, K> keyMapper, Function<? super T, V> valueMapper) {
        return io.vavr.collection.List.ofAll(iterable).toMap(keyMapper, valueMapper).toJavaMap();
    }

    /**
     * 找相同的item，并且组合消费
     *
     * @param sourceList 左侧源数组
     * @param matchList  用来匹配的右侧数组
     * @param predicate  左侧对象和右侧对象匹配一致的条件
     * @param ifConsumer 当匹配到右侧的对象的时候触发消费逻辑
     * @param <T>        源对象
     * @param <R>        用来匹配的对象
     */
    public static <T, R> void matchAndBundleFirst(Iterable<T> sourceList, Iterable<R> matchList, BiPredicate<T, R> predicate, BiConsumer<T, R> ifConsumer) {
        outer:
        for (T left : sourceList) {
            inner:
            for (R right : matchList) {
                if (predicate.test(left, right)) {
                    ifConsumer.accept(left, right);
                    continue outer;
                }
            }
        }
    }

    /**
     * 找相同的item，并且组合消费
     *
     * @param sourceList 左侧源数组
     * @param matchList  用来匹配的右侧数组
     * @param predicate  左侧对象和右侧对象匹配一致的条件
     * @param ifConsumer 当匹配到右侧的对象的时候触发消费逻辑
     * @param <T>        源对象
     * @param <R>        用来匹配的对象
     */
    public static <T, R> void matchAndBundleFirst(Iterable<T> sourceList, Supplier<Iterable<R>> matchList, BiPredicate<T, R> predicate, BiConsumer<T, R> ifConsumer) {
        outer:
        for (T left : sourceList) {
            inner:
            for (R right : matchList.get()) {
                if (predicate.test(left, right)) {
                    ifConsumer.accept(left, right);
                    continue outer;
                }
            }
        }
    }

    /**
     * 匹配matchList，找到满足条件的所有R集合，与当前匹配的T对象一起消费
     *
     * @param sourceList 左侧源数组
     * @param matchList  用来匹配的右侧数组
     * @param predicate  左侧对象和右侧对象匹配一致的条件
     * @param consumer   当匹配到右侧的对象的时候触发消费逻辑
     * @param <T>        源对象
     * @param <R>        用来匹配的对象
     */
    public static <T, R> void matchAndBundleList(Iterable<T> sourceList, Iterable<R> matchList, BiPredicate<T, R> predicate, BiConsumer<T, List<R>> consumer) {
        outer:
        for (T left : sourceList) {
            List<R> bundleList = new ArrayList<>();
            inner:
            for (R right : matchList) {
                if (predicate.test(left, right)) {
                    bundleList.add(right);
                }
            }
            consumer.accept(left, bundleList);
        }
    }

    /**
     * 匹配matchList，找到满足条件的所有R集合，与当前匹配的T对象一起消费
     *
     * @param sourceList 左侧源数组
     * @param matchList  用来匹配的右侧数组
     * @param predicate  左侧对象和右侧对象匹配一致的条件
     * @param consumer   当匹配到右侧的对象的时候触发消费逻辑
     * @param <T>        源对象
     * @param <R>        用来匹配的对象
     */
    public static <T, R> void matchAndBundleList(Iterable<T> sourceList, Supplier<Iterable<R>> matchList, BiPredicate<T, R> predicate, BiConsumer<T, List<R>> consumer) {
        for (T left : sourceList) {
            List<R> bundleList = new ArrayList<>();
            inner:
            for (R right : matchList.get()) {
                if (predicate.test(left, right)) {
                    bundleList.add(right);
                }
            }
            consumer.accept(left, bundleList);
        }
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

    /**
     * 延迟运行任务
     *
     * @param taskName      任务名称
     * @param taskRunner    运行任务, int参数为当前运行的第几次，序号从1开始, 返回结果表示是否继续运行下一次true:继续下一个, false:停止运行
     * @param delaySeconds  任务执行前的延迟秒数
     * @param periodSeconds 两次任务执行之间的间隔秒数, 仅当[limitCount > 0]时有效
     * @param limitCount    运行的最大次数, 必须大于等于1
     * @param blockAndWait  是否阻塞并等待完成
     * @throws InterruptedException
     */
    public static void runDelayed(String taskName, Function<Integer, Boolean> taskRunner, int delaySeconds, int periodSeconds, int limitCount, boolean blockAndWait) throws InterruptedException {
        Assert.state(limitCount >= 1, "[limitCount]运行次数必须大于等于1");
        log.info("【延迟运行任务-{}】共运行{}次, {}秒后开始, 间隔{}秒, 即将运行时间:{}", taskName, limitCount, delaySeconds, periodSeconds, DateTime.now().plusSeconds(delaySeconds).toString("yyyy-MM-dd HH:mm:ss"));
        CountDownLatch countDownLatch = new CountDownLatch(limitCount);
        AtomicBoolean continueNext = new AtomicBoolean(true);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            private int count = 1;

            @Override
            public void run() {
                if (count <= limitCount) {
                    try {
                        log.info("【间隔运行任务-{}】共运行{}次, 当前第{}次, 下次即将运行时间:{}", taskName, limitCount, count, DateTime.now().plusSeconds(periodSeconds).toString("HH:mm:ss"));
                        continueNext.set(taskRunner.apply(count));
                        if (!continueNext.get()) {
                            log.info("【手动停止任务-{}】共运行了{}次", taskName, count);
                            timer.cancel();
                            if (blockAndWait) {
                                while (countDownLatch.getCount() > 0) {
                                    countDownLatch.countDown();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                } else {
                    timer.cancel();
                }
                count++;
                countDownLatch.countDown();
            }
        };
        timer.schedule(timerTask, delaySeconds * 1000, periodSeconds * 1000);
        if (blockAndWait) {
            countDownLatch.await();
        }
    }

}
