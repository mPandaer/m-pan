package com.pandaer.pan.schedule.test;

import com.pandaer.pan.schedule.ScheduleManager;
import com.pandaer.pan.schedule.test.config.ScheduleTestConfig;
import com.pandaer.pan.schedule.test.task.SimpleScheduleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTestConfig.class)
public class ScheduleTest {

    @Autowired
    private ScheduleManager manager;

    @Autowired
    private SimpleScheduleTask task;

    @Test
    public void testSimpleTask() throws InterruptedException {
        String cron = "0/5 * * * * ? ";
        String key = manager.startTask(task, cron);
        Thread.sleep(10000L);
        cron = "0/2 * * * * ? ";
        manager.updateTask(key,cron);
        Thread.sleep(10000L);
        manager.deleteTask(key);

    }
}
