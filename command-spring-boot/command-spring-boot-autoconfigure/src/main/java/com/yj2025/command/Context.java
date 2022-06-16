package com.yj2025.command;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lmax.disruptor.dsl.ProducerType;
import com.yj2025.performance.BatchConsumer;
import com.yj2025.performance.Consumer;
import com.yj2025.performance.Producer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class Context {

    static ApplicationContext applicationContext;
    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }


    /**
     * 获取spring上下文中的bean对象
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> beanClass) {
        return Context.applicationContext.getBean(beanClass);
    }

    /**
     * 触发spring event事件
     *
     * @param event
     */
    public static void dispatchEvent(ApplicationEvent event) {
        Context.applicationContext.publishEvent(event);
    }

    /**
     * 开启手动事务执行
     *
     * @param action
     */
    public static void executeTransaction(java.util.function.Consumer<TransactionStatus> action) {
        TransactionTemplate transactionTemplate = Context.getBean(TransactionTemplate.class);
        transactionTemplate.executeWithoutResult(action);
    }

    /**
     * 开启手动事务执行并返回结果
     *
     * @param action
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
    public static <T> Producer<T> multi(Class<T> tClass, int threadNum, Consumer<T> consumer) {
        Producer<T> producer = Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(consumer.cloneSelfToMulti(threadNum))
                .requiredRingBufferSize(1024 * 64)
                .build();
        return producer;
    }


    /**
     * 批量消费发送到队列中的数据, 当sendData调用完毕后，建议调用{@link Producer#shutdown()}关闭当前多线程处理器。
     *
     * @param tClass        发送到队列的数据类型
     * @param batchConsumer 批量消费者模型， 建议设置批量数量在 500 ~ 3000 范围内。
     * @param <T>           发送的数据
     * @return 返回生产者，
     */
    public static <T> Producer<T> batch(Class<T> tClass, BatchConsumer<T> batchConsumer) {
        Producer<T> producer = Producer.builder()
                .optionnalProducerType(ProducerType.SINGLE)
                .requiredDataType(tClass)
                .requiredConsumers(batchConsumer)
                .requiredRingBufferSize(1024 * 64)
                .build();
        return producer;
    }

    /**
     * json序列化
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return wrapExceptions(() -> OBJECT_MAPPER.writeValueAsString(obj));
    }

    /**
     * json反序列化
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return wrapExceptions(() -> OBJECT_MAPPER.readValue(json, tClass));
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常
     *
     * @param runnable
     */
    public static void wrapExceptions(RunnableWrapper runnable) {
        wrapExceptions(runnable, Strings.EMPTY);
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常,同时指定message
     *
     * @param runnable
     * @param message
     */
    public static void wrapExceptions(RunnableWrapper runnable, String message) {
        try {
            runnable.run();
        } catch (java.lang.Exception e) {
            if (message != null && !"".equals(message)) {
                throw new RuntimeException(message + " " + e.getMessage(), e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常
     *
     * @param runnable
     * @param throwE
     */
    public static void wrapExceptions(RunnableWrapper runnable, RuntimeException throwE) {
        try {
            runnable.run();
        } catch (java.lang.Exception e) {
            if (throwE != null) {
                throw throwE;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常,并返回结果
     *
     * @param tSupplier
     * @param <T>
     * @return
     */
    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier) {
        return wrapExceptions(tSupplier, Strings.EMPTY);
    }

    /**
     * 捕获Exception异常,并抛出RuntimeException异常,并返回结果
     *
     * @param tSupplier
     * @param throwE
     * @param <T>
     * @return
     */
    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier, RuntimeException throwE) {
        try {
            return tSupplier.get();
        } catch (java.lang.Exception e) {
            if (throwE != null) {
                throw throwE;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 捕获Exception异常,并且抛出RuntimeException和指定异常message,并返回结果
     *
     * @param tSupplier
     * @param errMessage
     * @param <T>
     * @return
     */
    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier, String errMessage) {
        try {
            return tSupplier.get();
        } catch (java.lang.Exception e) {
            if (errMessage != null && !"".equals(errMessage)) {
                throw new RuntimeException(errMessage + " " + e.getMessage(), e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public interface SupplierWrapper<T> {
        T get() throws java.lang.Exception;
    }

    public interface RunnableWrapper {
        void run() throws java.lang.Exception;
    }

    /**
     * web请求的线程可以使用
     */
    public static class Web {

        private static HttpServletRequest getRequest() {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            }
            throw new RuntimeException("非web请求，无法获取request对象");
        }

        public static String getRequestHeader(String header) {
            return getRequest().getHeader(header);
        }

        public static String getEntCode() {
            return getRequest().getHeader(URLDecoder.decode("entCode", StandardCharsets.UTF_8));
        }

        public static String getEntName() {
            return getRequest().getHeader(URLDecoder.decode("entName", StandardCharsets.UTF_8));
        }

        public static String getUserCode() {
            return getRequest().getHeader(URLDecoder.decode("userCode", StandardCharsets.UTF_8));
        }

        public static String getUserName() {
            return getRequest().getHeader(URLDecoder.decode("userName", StandardCharsets.UTF_8));
        }

        public static String getAccountCode() {
            return getRequest().getHeader(URLDecoder.decode("accountCode", StandardCharsets.UTF_8));
        }

        public static String getAccountName() {
            return getRequest().getHeader(URLDecoder.decode("accountName", StandardCharsets.UTF_8));
        }
    }


}
