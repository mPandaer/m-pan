package com.pandaer.pan.stream.core;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;

import java.util.Objects;

@Log4j2
public abstract class AbstractConsumer {

    protected void printLog(Message<?> message) {
        log.info("{} start consume the message, the message is {}",this.getClass().getSimpleName(),message);
    }

    protected boolean isEmptyMessage(Message<?> message) {
        if (Objects.isNull(message)) {
            return true;
        }
        Object payload = message.getPayload();
        if (payload == null) {
            return true;
        }
        return false;
    }
}
