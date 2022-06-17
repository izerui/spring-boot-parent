package com.yj2025.command;

public interface Command<R> {

    /**
     * 执行命令
     *
     * @return
     * @throws Exception
     */
    Command<R> execute();

    /**
     * @return
     */
    R getResult();

}
