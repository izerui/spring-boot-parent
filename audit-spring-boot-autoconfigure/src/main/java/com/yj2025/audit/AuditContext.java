package com.yj2025.audit;

/**
 * Created by serv on 2016/12/8.
 */
public interface AuditContext {

    /**
     * 操作记录
     *
     * @param record 记录的内容
     */
    void record(Record record);
}
