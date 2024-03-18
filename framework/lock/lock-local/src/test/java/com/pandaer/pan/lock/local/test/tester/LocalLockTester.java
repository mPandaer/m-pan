package com.pandaer.pan.lock.local.test.tester;

import com.pandaer.pan.lock.core.annotation.Lock;
import org.springframework.stereotype.Component;

@Component
public class LocalLockTester {
    @Lock(name = "test",keys = "#name",expireSeconds = 10L)
    public String test(String name) {
        System.out.println(Thread.currentThread().getName() + " get lock");
        String tip = "Hello " + name;
        System.out.println(Thread.currentThread().getName() + " release lock");
        return tip;
    }
}
