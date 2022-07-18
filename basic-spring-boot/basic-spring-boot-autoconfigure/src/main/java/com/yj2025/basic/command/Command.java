package com.yj2025.basic.command;

import org.slf4j.Logger;

public interface Command<R> {

    /**
     * 执行命令
     */
    R execute();

    /**
     * 是否执行过
     */
    boolean isExecuted();

    /**
     * 执行耗时
     */
    Long getTimeMillis();

    /**
     * 获取日志对象
     * @return
     */
    Logger getLogger();
}
