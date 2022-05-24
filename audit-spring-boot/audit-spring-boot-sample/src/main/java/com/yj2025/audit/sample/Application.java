package com.yj2025.audit.sample;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class Application {

    @Bean
    public Queue queue() {
        Queue queue = new Queue("test.audit", true, false, false, Collections.EMPTY_MAP);
        return queue;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("test", true, false);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange())
                .with("test.audit");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
