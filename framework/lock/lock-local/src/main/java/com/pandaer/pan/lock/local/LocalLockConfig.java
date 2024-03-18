package com.pandaer.pan.lock.local;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@SpringBootConfiguration
@Log4j2
public class LocalLockConfig {

    @Bean
    public LockRegistry localLockRegistry() {
        LockRegistry lockRegistry = new DefaultLockRegistry();
        log.info("初始化本地锁");
        return lockRegistry;
    }
}
