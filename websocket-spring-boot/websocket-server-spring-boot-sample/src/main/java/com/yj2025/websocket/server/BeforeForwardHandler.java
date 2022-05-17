package com.yj2025.websocket.server;

import com.yj2025.websocket.WebMsg;
import org.springframework.stereotype.Component;

@Component
public class BeforeForwardHandler implements OnBeforeForwardMessageHandler {
    @Override
    public void handler(WebMsg msg) {
        System.out.println(msg.toString());
    }
}
