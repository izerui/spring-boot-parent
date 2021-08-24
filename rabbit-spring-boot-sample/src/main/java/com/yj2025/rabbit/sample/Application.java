package com.yj2025.rabbit.sample;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public Queue queue() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x-dead-letter-exchange", "dead");//设置死信交换机
        map.put("x-dead-letter-routing-key", "dead");//设置死信routingKey
        Queue queue = new Queue("test.queue001",true, false, false, map);
        return queue;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("test", true, false);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange())
                .with("test.queue001");
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Map map = new HashMap();
        map.put("type", "测试");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                rabbitTemplate.convertAndSend("test", "test.queue001", map);
            }
        }, 5000,1000);
    }
}
