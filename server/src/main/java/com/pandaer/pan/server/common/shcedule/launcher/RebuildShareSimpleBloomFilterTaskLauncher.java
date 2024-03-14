package com.pandaer.pan.server.common.shcedule.launcher;


import com.pandaer.pan.schedule.ScheduleManager;
import com.pandaer.pan.server.common.task.CleanExpireChunkFileTask;
import com.pandaer.pan.server.common.task.RebuildShareSimpleBloomFilterTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * 启动器 - 启动重建布隆过滤器定时任务
 */

@Component
@Log4j2
public class RebuildShareSimpleBloomFilterTaskLauncher implements CommandLineRunner {

    private static final String CRON = "0 0 0 * * ?";
//    private static final String CRON = "0/5 * * * * ?"; test cron

    @Autowired
    private RebuildShareSimpleBloomFilterTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task,CRON);
    }
}
