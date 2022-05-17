package thread.pool;

import java.util.concurrent.atomic.AtomicInteger;

public class MainTests {

    public static void main(String[] args) throws InterruptedException {
        long l = System.currentTimeMillis();
        AtomicInteger atomicLong = new AtomicInteger(1);
        while ((l + 10000) > System.currentTimeMillis()) { // 10秒
            final long andIncrement = atomicLong.getAndIncrement();
            System.out.println(andIncrement);
            Thread.sleep(10);
        }
    }
}
