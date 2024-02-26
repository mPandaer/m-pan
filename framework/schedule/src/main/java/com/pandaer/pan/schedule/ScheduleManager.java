package com.pandaer.pan.schedule;

import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.UUIDUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Log4j2
public class ScheduleManager {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private final Map<String,ScheduleHolder> cache = new ConcurrentHashMap<>();

    public String startTask(ScheduleTask task,String cron) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cron));
        String key = UUIDUtil.getUUID();
        ScheduleHolder holder = new ScheduleHolder(task,future);
        cache.put(key,holder);
        log.info("启动任务"+task.getName()+"成功,UUID为：" + key);
        return key;
    }

    public void deleteTask(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new MPanFrameworkException("key不能为空");
        }
        ScheduleHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            throw new MPanFrameworkException("定时任务不存在");
        }
        holder.getFuture().cancel(true);
        cache.remove(key);
        log.info("任务("+holder.getTask().getName()+")停止成功 UUID: " + key);
    }

    public void updateTask(String key,String cron) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(cron)) {
            throw new MPanFrameworkException("key or cron 不能为空");
        }
        ScheduleHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            throw new MPanFrameworkException("定时任务不存在");
        }
        holder.getFuture().cancel(true);
        ScheduledFuture<?> future = taskScheduler.schedule(holder.getTask(), new CronTrigger(cron));
        holder.setFuture(future);
    }
}
