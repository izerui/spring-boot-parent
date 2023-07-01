package com.yj2025.redis.sample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import jakarta.annotation.Resource;
import java.util.Date;


@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        // test 1
        this.testStringRedisTemplate();
        System.out.println("================================================================");

        // test2
        this.testRedisTemplate();
    }

    private void testStringRedisTemplate(){
        String key = "test_redis_key";
        stringRedisTemplate.opsForValue().set(key, "test_redis_value");
        System.out.println(stringRedisTemplate.opsForValue().get(key));
    }

    private void testRedisTemplate(){
        UserTest userTest = new UserTest(1, "张三", new Date());
        redisTemplate.opsForHash().put("user", "1", userTest);
        Object user = redisTemplate.opsForHash().get("user", "1");
        assert user != null;
        System.out.println(user.toString());
    }


}
