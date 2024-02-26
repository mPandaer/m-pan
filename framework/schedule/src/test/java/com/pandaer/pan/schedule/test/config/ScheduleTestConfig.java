package com.pandaer.pan.schedule.test.config;

import com.pandaer.pan.core.constants.MPanConstants;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH + ".schedule")
public class ScheduleTestConfig {
}
