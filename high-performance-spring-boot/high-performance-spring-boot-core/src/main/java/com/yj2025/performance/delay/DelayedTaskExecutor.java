package com.yj2025.performance.delay;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.*;

/**
 * 延迟任务处理器
 *
 * @author serv
 * @date 2022/5/24
 */
public class DelayedTaskExecutor implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DelayedTaskExecutor.class);

    private String name;

    private ThreadPoolExecutor executor;

    private volatile boolean running;

    private ThreadPoolExecutor putExecutor;

    private DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();

    public DelayedTaskExecutor(String name) {
        this.name = name;
    }

    public void submit(Runnable runnable, long delayedTime) {
        DelayedTask task = new DelayedTask(runnable, delayedTime);
        delayQueue.offer(task);
        logger.debug("{}添加延迟任务 {} 延迟{}ms", this.name, task, delayedTime);
    }

    @Override
    public void destroy() throws Exception {
        running = false;

        if (putExecutor != null) {
            putExecutor.shutdown();
            boolean shutdown = putExecutor.awaitTermination(8000, TimeUnit.MILLISECONDS);
            if (!shutdown) {
                List<Runnable> notRunList = putExecutor.shutdownNow();
                if (CollectionUtils.isNotEmpty(notRunList)) {
                    logger.warn("强制关闭延迟任务转移线程...未执行任务数量{}", notRunList.size());
                }
            }
        }
        if (executor != null) {
            executor.shutdown();
            boolean shutdown = executor.awaitTermination(8000, TimeUnit.MILLISECONDS);
            if (!shutdown) {
                List<Runnable> notRunList = executor.shutdownNow();
                if (CollectionUtils.isNotEmpty(notRunList)) {
                    logger.warn("强制关闭延迟任务执行线程池...未执行任务数量{}", notRunList.size());
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        running = true;

        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
                Runtime.getRuntime().availableProcessors() * 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat(this.name + "-pool-%d")
                        .setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                            @Override
                            public void uncaughtException(Thread t, Throwable e) {
                                logger.error("延迟任务执行线程异常,thread={}", t, e);
                            }
                        })
                        .build());

        putExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat(this.name + "-submiter")
                        .setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                            @Override
                            public void uncaughtException(Thread t, Throwable e) {
                                logger.error("延迟任务转移线程异常,thread={}", t, e);
                            }
                        })
                        .build());

        putExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (running || delayQueue.size() > 0) {
                    try {
                        DelayedTask task = delayQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if (task == null) {
                            continue;
                        }
                        logger.debug("开始执行延迟任务 {}", task);
                        executor.execute(task.getRunnable());
                    } catch (InterruptedException e) {
                        logger.error("延迟任务转移线程异常interrupted", e);
                    } catch (Throwable e) {
                        logger.error("延迟任务转移线程异常", e);
                    }
                }
                logger.debug("延迟任务转移线程退出");
            }
        });
    }

}
