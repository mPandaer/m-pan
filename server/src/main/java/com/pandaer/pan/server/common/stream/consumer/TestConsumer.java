package com.pandaer.pan.server.common.stream.consumer;

import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.TestEvent;
import com.pandaer.pan.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer extends AbstractConsumer {

    @StreamListener(PanChannels.TEST_INPUT)
    public void testInput(Message<TestEvent> message) {
        printLog(message);
    }
}
