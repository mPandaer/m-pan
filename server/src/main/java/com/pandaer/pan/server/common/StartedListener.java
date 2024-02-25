package com.pandaer.pan.server.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class StartedListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        String port = applicationContext.getEnvironment().getProperty("server.port");
        String url = "http://localhost:" + port;
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, url));
        if (enableSwagger2(applicationContext)) {
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, url + "/doc.html"));
        }
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "M-Pan started successfully!"));
    }

    private boolean enableSwagger2(ConfigurableApplicationContext applicationContext) {
        return applicationContext.getEnvironment().getProperty("swagger2.enable", Boolean.class, true)
                && applicationContext.containsBean("swagger2Config");
    }
}
