package com.yj2025.websocket.server;

import com.yj2025.websocket.WebMsg;

public interface OnAfterForwardMessageHandler {
    void handler(WebMsg msg);
}
