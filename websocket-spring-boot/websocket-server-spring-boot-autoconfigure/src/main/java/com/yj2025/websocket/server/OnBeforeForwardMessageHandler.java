package com.yj2025.websocket.server;

import com.yj2025.websocket.WebMsg;

public interface OnBeforeForwardMessageHandler {
    void handler(WebMsg msg);
}
