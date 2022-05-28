package com.yj2025.performance.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedTask implements Delayed {

    // 提交时间
    private long submitTime;

    private long runningTime;

    private Runnable runnable;

    public DelayedTask(Runnable runnable, long delayTime) {
        this.runnable = runnable;
        this.submitTime = System.currentTimeMillis();
        this.runningTime = submitTime + delayTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(runningTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == null || !(o instanceof DelayedTask)) {
            return 1;
        }
        if (o == this) {
            return 0;
        }
        DelayedTask otherTask = (DelayedTask) o;
        if (this.runningTime > otherTask.runningTime) {
            return 1;
        } else if (this.runningTime == otherTask.runningTime) {
            return 0;
        } else {
            return -1;
        }
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
