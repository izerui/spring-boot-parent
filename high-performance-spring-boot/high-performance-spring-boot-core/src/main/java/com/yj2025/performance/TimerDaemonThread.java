package com.yj2025.performance;

import org.apache.commons.lang3.RandomUtils;

import java.util.TimerTask;

public class TimerDaemonThread extends Thread {

    private final TimerTask task;
    private final long delay;
    private transient long executionTime;
    private transient boolean actived;

    public TimerDaemonThread(final TimerTask task, final long delay) {
        this.setName("Timer-" + task.getClass().getSimpleName() + RandomUtils.nextInt(1, 100));
        this.setDaemon(true);
        this.task = task;
        this.delay = delay;
    }

    public void next() {
        this.actived = true;
        if (this.executionTime <= System.currentTimeMillis()) {
            this.executionTime = System.currentTimeMillis() + delay;
        }
    }

    public void pause() {
        this.actived = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!actived) {
                    continue;
                }
                long currentTime = System.currentTimeMillis();
                if (executionTime > currentTime) {
                    Thread.sleep(executionTime - currentTime);
                }
                task.run();
                actived = false;
            } catch (InterruptedException e) {
            }
        }
    }

}
