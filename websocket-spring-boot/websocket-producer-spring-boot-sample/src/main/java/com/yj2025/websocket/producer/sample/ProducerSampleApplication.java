package com.yj2025.websocket.producer.sample;

import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.producer.WebSocketContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducerSampleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ProducerSampleApplication.class, args);
    }

    @Autowired
    private WebSocketContext context;

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 10; i++) {
            WebMsg webMsg = new WebMsg("entCode001", "userCode001");
            webMsg.set("a", "" + i);
            context.sendMessage(webMsg);
        }
        context.destroy();
    }
}
