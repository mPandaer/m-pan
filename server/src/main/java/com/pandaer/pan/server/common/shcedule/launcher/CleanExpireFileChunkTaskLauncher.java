package com.pandaer.pan.server.common.shcedule.launcher;


import com.pandaer.pan.schedule.ScheduleManager;
import com.pandaer.pan.server.common.task.CleanExpireChunkFileTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * 清理过期文件分块任务启动器
 */

@Component
@Log4j2
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {

    private static final String CRON = "0 0 0 * * ?";
//    private static final String CRON = "0/5 * * * * ?"; test cron

    @Autowired
    private CleanExpireChunkFileTask cleanExpireChunkFileTask;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(cleanExpireChunkFileTask,CRON);
    }
}
