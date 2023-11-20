package com.yj2025.sequence;

import com.yj2025.lock.Lock;
import com.yj2025.sequence.storage.MysqlNumberStorage;
import com.yj2025.sequence.storage.RedisNumberStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SequenceAutoConfiguration {

    @Bean
    public RedisNumberStorage sequenceNumberStorage(StringRedisTemplate redisTemplate) {
        return new RedisNumberStorage(redisTemplate);
    }

    @Bean
    public MysqlNumberStorage mysqlNumberStorage(PlatformTransactionManager transactionManager, Lock lock, JdbcTemplate jdbcTemplate) {
        return new MysqlNumberStorage(transactionManager, lock, jdbcTemplate);
    }


//    @Bean
//    @ConditionalOnProperty(value = "sequence.storage.type", matchIfMissing = true, havingValue = "redis")
//    public SequenceService sequenceService(RedisNumberStorage numberStorage, Lock lock) {
//        return new SequenceService(numberStorage, lock);
//    }
//
//    @Bean
//    @ConditionalOnProperty(value = "sequence.storage.type", havingValue = "mysql")
//    public SequenceService sequenceService(MysqlNumberStorage numberStorage, Lock lock) {
//        return new SequenceService(numberStorage, lock);
//    }
}
