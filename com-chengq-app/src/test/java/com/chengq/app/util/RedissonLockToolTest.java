package com.chengq.app.util;

import com.chengq.app.Application;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class RedissonLockToolTest {

    @Autowired
    private RedissonLockTool redissonLockTool;

    @Test
    void testWithLockMutualExclusion() throws Exception {
        String lockKey = "redissonLockToolTest:" + System.currentTimeMillis();

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger inCritical = new AtomicInteger(0);
        AtomicBoolean violation = new AtomicBoolean(false);

        Runnable task = () -> {
            ready.countDown();
            try {
                go.await();
                redissonLockTool.withLock(
                        lockKey,
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(3),
                        () -> {
                            int cur = inCritical.incrementAndGet();
                            if (cur > 1) {
                                violation.set(true);
                            }
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                inCritical.decrementAndGet();
                            }
                        }
                );
            } catch (Exception e) {
                // test framework will surface as assertion failure in outer thread
                violation.set(true);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        ready.await();
        go.countDown();

        t1.join();
        t2.join();

        Assertions.assertFalse(violation.get(), "Lock should prevent concurrent execution");
    }
}

