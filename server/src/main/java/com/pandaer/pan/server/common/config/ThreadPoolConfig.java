package com.pandaer.pan.server.common.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootConfiguration
public class ThreadPoolConfig {

    @Bean("eventListenerTaskExecutor")
    public ThreadPoolTaskExecutor eventListenerTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(10);
        threadPoolExecutor.setMaxPoolSize(10);
        threadPoolExecutor.setKeepAliveSeconds(200);
        threadPoolExecutor.setQueueCapacity(2048);
        threadPoolExecutor.setThreadNamePrefix("event-listener-thread");
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

}
