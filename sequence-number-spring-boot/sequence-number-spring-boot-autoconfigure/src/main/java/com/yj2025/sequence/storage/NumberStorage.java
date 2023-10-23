package com.yj2025.sequence.storage;

import com.yj2025.sequence.PeriodType;

public interface NumberStorage {

    /**
     * 按指定时间段获取可用的顺序号(优先已回收的顺序号)
     *
     * @param groupId
     * @param period
     * @return
     */
    Integer getNumber(String groupId, PeriodType.Period period);

    /**
     * 按指定时间段回收指定顺序号
     *
     * @param groupId
     * @param period
     * @param number
     */
    void recycleNumber(String groupId, PeriodType.Period period, Integer number);


    /**
     * 验证指定时间段指定的序号是否可用
     *
     * @param groupId
     * @param period
     * @param number
     * @return
     */
    boolean verifyValidNumber(String groupId, PeriodType.Period period, Integer number);

}
