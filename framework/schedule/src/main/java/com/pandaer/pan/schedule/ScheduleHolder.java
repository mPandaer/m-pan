package com.pandaer.pan.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ScheduledFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleHolder {

    private ScheduleTask task;

    private ScheduledFuture<?> future;
}
