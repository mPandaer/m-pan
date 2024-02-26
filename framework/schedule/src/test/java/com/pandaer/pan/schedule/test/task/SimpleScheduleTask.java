package com.pandaer.pan.schedule.test.task;


import com.pandaer.pan.schedule.ScheduleTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SimpleScheduleTask implements ScheduleTask {
    @Override
    public String getName() {
        return "test-task";
    }

    @Override
    public void run() {
        log.info(getName() + "run...");
    }
}
