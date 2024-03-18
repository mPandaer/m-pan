package com.pandaer.pan.lock.local.test;

import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.lock.core.LockConstants;
import com.pandaer.pan.lock.local.test.tester.LocalLockTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH + ".lock")
public class LocalLockTest {

    @Autowired
    private LockRegistry lockRegistry;

    @Autowired
    private LocalLockTester localLockTester;

    private final ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    @Test
    public void testLockRegistry() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(() -> {
                Lock lock = lockRegistry.obtain(LockConstants.M_PAN_LOCK);
                boolean lockRes = false;
                try {
                    lockRes = lock.tryLock();
                    if (lockRes) {
                        System.out.println(Thread.currentThread().getName() + " get lock");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    if (lockRes) {
                        System.out.println(Thread.currentThread().getName() + " release lock");
                        lock.unlock();
                    }
                }
                localLockTester.test("pandaer");
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLocalTester() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(() -> {
                localLockTester.test("pandaer");
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
