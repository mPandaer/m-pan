package com.pandaer.pan.server.common.event.test;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TestEvent extends ApplicationEvent {
    private final String message;

    public TestEvent(Object source,String message) {
        super(source);
        this.message = message;
    }
}
