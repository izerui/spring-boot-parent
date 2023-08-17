package com.yj2025.basic.processor;

import com.yj2025.basic.support.ApplicationBeanAware;
import com.yj2025.basic.support.ColorOutput;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 抽象处理器
 *
 * @param <T>
 */
public abstract class AbstractProcessor<T> implements ApplicationBeanAware {

    private boolean executed = false;
    private Long executeTimeMillis;

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public AbstractProcessor() {
        autowiredBean();
    }

    /**
     * 链式调用入口
     *
     * @param request
     * @param chain
     * @throws Exception
     */
    final void doProcess(T request, ProcessorChain chain) throws Exception {
        if (executed) {
            throw new RuntimeException("process: " + this.getClass().getName() + " 已经执行过,不允许重复执行!");
        }
        beforeDoExecute(request, chain);
        long startTime = System.currentTimeMillis();
        boolean process = this.process(request, chain);
        afterExecuted(request, chain);
        executeTimeMillis = System.currentTimeMillis() - startTime;
        if (executeTimeMillis > getLimitWarnningTimeMillis()) {
            logger.warn(ColorOutput.BRIGHT_RED("警告：{} 耗时: {} (ms)"), this.getClass().getSimpleName(), executeTimeMillis);
        } else {
            logger.debug("{} 耗时: {} (ms)", this.getClass().getSimpleName(), executeTimeMillis);
        }
        executed = true;
        if (process) {
            chain.doProcess(request);
        }
    }

    protected void autowiredBean() {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        Arrays.asList(declaredFields).forEach(field -> {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            boolean anyMatch = Arrays.asList(annotations).stream().map(Annotation::annotationType)
                    .anyMatch(aClass -> aClass.isAssignableFrom(Autowired.class) || aClass.isAssignableFrom(Resource.class));
            if (anyMatch) {
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    ReflectionUtils.setField(field, this, $(qualifier.value()));
                } else {
                    ReflectionUtils.setField(field, this, $(field.getType()));
                }
            }
        });
    }

    /**
     * 前置校验器，需要自行throw exception
     *
     * @return
     */
    protected void beforeDoExecute(T request, ProcessorChain chain) {
        /** no op */
    }

    /**
     * 后置处理器，比如记录日志啥的
     */
    protected void afterExecuted(T request, ProcessorChain chain) throws Exception {
        /** no op */
    }

    /**
     * 实际执行
     *
     * @param request
     * @param chain
     * @return true: 继续执行 false: 跳出当前链
     * @throws Exception
     */
    protected abstract boolean process(T request, ProcessorChain chain) throws Exception;

    /**
     * 是否跳过异常，继续执行
     *
     * @return
     */
    protected boolean skipException() {
        return false;
    }

    /**
     * 是否执行当前process
     *
     * @param request
     * @return
     */
    protected boolean isSupported(T request) {
        return true;
    }

    /**
     * 超过该阈值限制的时间会告警
     *
     * @return
     */
    protected long getLimitWarnningTimeMillis() {
        return 500L;
    }

    /**
     * 是否执行过
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * 执行耗时
     */
    public Long getTimeMillis() {
        return executeTimeMillis;
    }
}
