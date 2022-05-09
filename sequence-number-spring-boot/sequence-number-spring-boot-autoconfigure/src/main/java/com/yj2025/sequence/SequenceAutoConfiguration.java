package com.yj2025.sequence;

import com.yj2025.lock.Lock;
import com.yj2025.sequence.storage.NumberStorage;
import com.yj2025.sequence.storage.RedisNumberStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class SequenceAutoConfiguration {

    @Bean
    public NumberStorage sequenceNumberStorage(StringRedisTemplate redisTemplate) {
        return new RedisNumberStorage(redisTemplate);
    }


    @Bean
    public SequenceService sequenceService(NumberStorage numberStorage, Lock lock) {
        return new SequenceService(numberStorage, lock);
    }
}
