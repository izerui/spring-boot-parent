package thread.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolTests {

    // https://www.cnblogs.com/dafanjoy/p/9729358.html
    private static ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(
            // 核心线程数
            5,
            // 最大线程数
            16,
            // 大于核心线程数量的空闲线程于30毫秒后销毁
            0,
            // 空闲超时的单位毫秒
            TimeUnit.MILLISECONDS,
            // 有界的任务队列缓冲区 大小为25个等待任务 (当核心线程空闲后，放入核心线程，缓冲区满了后，新任务直接以最大线程限制内去执行。如果最大线程也满了，缓冲区也满了，则执行相应的拒绝策略)
            new ArrayBlockingQueue<Runnable>(25),
            // 线程工厂
            Executors.defaultThreadFactory(),
            // 都满了的情况下打印警告，并直接丢弃
            new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        AtomicInteger atomicLong = new AtomicInteger(1);
        while ((l + 10000) > System.currentTimeMillis()) { // 10秒
            POOL_EXECUTOR.execute(() -> {
                try {
                    Thread.sleep(10);
                    long andIncrement = atomicLong.getAndIncrement();
                    System.out.println(Thread.currentThread().getName() + " -- " + andIncrement);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        POOL_EXECUTOR.shutdown();
    }
}
