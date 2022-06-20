package com.yj2025.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommand<R> implements Command<R> {

    private boolean executed = false;
    private Long executeTimeMillis;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 前置校验器，需要自行throw exception
     *
     * @return
     */
    protected void beforeDoExecute() {
        /** no op */
    }


    @Override
    public final R execute() {
        if (executed) {
            throw new RuntimeException("command: " + this.getClass().getName() + " 已经执行过,不允许重复执行!");
        }
        long startTime = System.currentTimeMillis();
        R r;
        try {
            beforeDoExecute();
            r = doExecute();
            executed = true;
            afterExecuted(r);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            executeTimeMillis = System.currentTimeMillis() - startTime;
            if (executeTimeMillis > 500) {
                logger.warn("警告：{} 耗时: {}(ms)", this.getClass().getName(), executeTimeMillis);
            } else {
                logger.debug("{} 耗时: {}(ms)", this.getClass().getName(), executeTimeMillis);
            }
        }
        return r;
    }

    /**
     * 执行器
     *
     * @return 返回结果
     */
    protected abstract R doExecute() throws Exception;

    /**
     * 后置处理器，比如记录日志啥的
     */
    protected void afterExecuted(R result) throws Exception {
        /** no op */
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public Long getTimeMillis() {
        return executeTimeMillis;
    }

}
