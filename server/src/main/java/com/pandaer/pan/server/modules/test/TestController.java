package com.pandaer.pan.server.modules.test;

import com.pandaer.pan.server.common.event.test.TestEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @RequestMapping("/test")
    public String test() {
        applicationContext.publishEvent(new TestEvent(this,"test test"));
        return "DONE";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
