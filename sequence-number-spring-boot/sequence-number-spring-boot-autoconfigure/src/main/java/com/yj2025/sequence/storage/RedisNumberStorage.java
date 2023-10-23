package com.yj2025.sequence.storage;

import com.yj2025.sequence.PeriodType;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public class RedisNumberStorage implements NumberStorage {

    private final static String SEQUENCE_KEY_PATH = "sequence-number:%s:%s";

    private StringRedisTemplate redisTemplate;

    public RedisNumberStorage(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Integer getNumber(String groupId, PeriodType.Period period) {
        String redisKey = String.format(SEQUENCE_KEY_PATH, groupId, period.getPeriodFormatter());
        BoundZSetOperations<String, String> operations = redisTemplate.boundZSetOps(redisKey);
        // 优先使用已回收的
        Set<String> negativeSets = operations.reverseRangeByScore(Integer.MIN_VALUE, -1);
        if (negativeSets != null && !negativeSets.isEmpty()) {
            String validObject = negativeSets.iterator().next();
            operations.remove(validObject);
            operations.add(validObject, Double.valueOf(validObject));
            return Integer.valueOf(validObject);
        }
        // 已有的递增
        Set<String> sets = operations.reverseRangeByScore(1, Integer.MAX_VALUE);
        if (sets != null && !sets.isEmpty()) {
            String firstValidObject = sets.iterator().next();
            String incrementValue = String.valueOf(Integer.valueOf(firstValidObject) + 1);
            operations.add(incrementValue, Double.valueOf(incrementValue));
            return Integer.valueOf(incrementValue);
        }
        // 从未产生过序号
        operations.add("1", 1D);
        return 1;
    }

    @Override
    public void recycleNumber(String groupId, PeriodType.Period period, Integer number) {
        String redisKey = String.format(SEQUENCE_KEY_PATH, groupId, period.getPeriodFormatter());
        BoundZSetOperations<String, String> operations = redisTemplate.boundZSetOps(redisKey);
        operations.remove(number.toString());
        operations.add(number.toString(), Math.negateExact(number));
    }

    @Override
    public boolean verifyValidNumber(String groupId, PeriodType.Period period, Integer number) {
        String redisKey = String.format(SEQUENCE_KEY_PATH, groupId, period.getPeriodFormatter());
        BoundZSetOperations<String, String> operations = redisTemplate.boundZSetOps(redisKey);
        Double score = operations.score(number.toString());
        return score == null || score < 0;
    }
}
