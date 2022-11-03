package com.yj2025.sequence;

import com.yj2025.lock.Lock;
import com.yj2025.sequence.storage.NumberStorage;
import org.springframework.util.Assert;

import java.util.function.Consumer;

public class SequenceService {

    private NumberStorage numberStorage;
    private Lock lock;

    public SequenceService(NumberStorage numberStorage, Lock lock) {
        this.numberStorage = numberStorage;
        this.lock = lock;
    }

    /**
     * 获取并消费指定时间段的序号
     *
     * @param groupId  业务分组ID
     * @param period   时间段
     * @param consumer 回调函数
     */
    public void consumerNumber(String groupId, PeriodType.Period period, Consumer<Integer> consumer) {
        lock.execute(groupId + "_" + period.getPeriodFormater(), 60, () -> {
            Integer number = null;
            try {
                number = numberStorage.getNumber(groupId, period);
                Assert.notNull(number, groupId + "获取的序号不能为空");
                consumer.accept(number);
            } catch (Exception ex) {
                numberStorage.recycleNumber(groupId, period, number);
                throw ex;
            }
        }, e -> new RuntimeException("在地方就是"));
    }

    /**
     * 回收指定时间段的指定序号
     *
     * @param groupId 业务分组ID
     * @param period  时间段
     * @param number  序号
     */
    public void recycleNumber(String groupId, PeriodType.Period period, Integer number) {
        numberStorage.recycleNumber(groupId, period, number);
    }


    /**
     * 验证指定时间段的指定序号是否可用
     *
     * @param groupId 业务分组ID
     * @param period  时间段
     * @param number  序号
     * @return
     */
    public boolean verifyNumber(String groupId, PeriodType.Period period, Integer number) {
        return numberStorage.verifyVaildNumber(groupId, period, number);
    }

}
