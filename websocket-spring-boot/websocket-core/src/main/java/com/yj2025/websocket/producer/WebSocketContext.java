package com.yj2025.websocket.producer;

import com.yj2025.websocket.WebMsg;

public interface WebSocketContext {

    void sendMessage(WebMsg webMsg);

    void destroy() throws Exception;
}
