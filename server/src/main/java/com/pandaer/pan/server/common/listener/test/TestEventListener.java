package com.pandaer.pan.server.common.listener.test;

import com.pandaer.pan.server.common.event.test.TestEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TestEventListener {

    @EventListener(TestEvent.class)
    @Async("eventListenerTaskExecutor")
    public void handleEventListener(TestEvent event) throws InterruptedException {
        String message = event.getMessage();
        Thread.sleep(2000L);
        log.info("Thread name is {} message is {}",Thread.currentThread().getName(),message);
    }
}
