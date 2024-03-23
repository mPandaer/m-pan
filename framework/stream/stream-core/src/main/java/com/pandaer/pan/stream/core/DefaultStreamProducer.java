package com.pandaer.pan.stream.core;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("defaultStreamProducer")
public class DefaultStreamProducer extends AbstractStreamProducer{
    @Override
    protected void afterSend(Message<Object> message, boolean res) {

    }

    @Override
    protected void preSend(Message<Object> message) {

    }
}
