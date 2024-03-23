package com.pandaer.pan.server.modules.test;

import com.pandaer.pan.server.common.event.test.TestEvent;
import com.pandaer.pan.stream.core.IStreamProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("defaultStreamProducer")
    private IStreamProducer streamProducer;

    @RequestMapping("/test")
    public String test() {
        applicationContext.publishEvent(new TestEvent(this,"test test"));
        return "DONE";
    }

    @RequestMapping("stream/test")
    public String streamTest(String name) {
        com.pandaer.pan.server.common.stream.event.TestEvent testEvent = new com.pandaer.pan.server.common.stream.event.TestEvent();
        testEvent.setMessage(name);
        streamProducer.sendMessage("testInput",testEvent);
        return "DONE";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
