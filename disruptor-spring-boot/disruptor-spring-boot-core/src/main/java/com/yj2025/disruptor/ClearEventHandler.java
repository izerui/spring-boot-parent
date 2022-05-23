package com.yj2025.disruptor;

import com.lmax.disruptor.EventHandler;

public class ClearEventHandler<T> implements EventHandler<T> {
    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        event = null;
    }
}
