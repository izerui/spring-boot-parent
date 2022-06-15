package com.yj2025.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommand<T, R> implements Command<R> {

    private T parameter;
    private R result;

    private boolean executed = false;
    private Long executeTimeMillis;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractCommand(T parameter) {
        this.parameter = parameter;
    }

    public final T getParameter() {
        return parameter;
    }

    @Override
    public final R getResult() {
        return result;
    }

    private void setResult(R r) {
        this.result = r;
    }

    /**
     * 前置校验器，需要自行throw exception
     *
     * @param parameter 入参
     * @return
     */
    protected void validatingBeforeExecute(T parameter) {
        /** no op */
    }


    @Override
    public final void execute() {
        if (executed) {
            throw new RuntimeException("command: " + this.getClass().getName() + " 已经执行过,不允许重复执行!");
        }
        long startTime = System.currentTimeMillis();
        validatingBeforeExecute(getParameter());
        try {
            R r = doExecute(getParameter());
            setResult(r);
            afterExecuted(getParameter(), getResult());
            executed = true;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            executeTimeMillis = System.currentTimeMillis() - startTime;
            logger.debug("{} 耗时(ms): {}", this.getClass().getName(), executeTimeMillis);
        }
    }

    /**
     * 执行器
     *
     * @param parameter 入参
     * @return 返回结果
     * @throws Exception
     */
    protected abstract R doExecute(T parameter) throws Exception;

    /**
     * 后置处理器，比如记录日志啥的
     *
     * @throws Exception
     */
    protected void afterExecuted(T parameter, R result) throws Exception {
        /** no op */
    }
}
