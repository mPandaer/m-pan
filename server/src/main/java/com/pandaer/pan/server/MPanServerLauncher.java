package com.pandaer.pan.server;

import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.server.common.stream.channel.PanChannels;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH)
@ServletComponentScan(basePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH + ".server.modules.**.mapper")
@EnableAsync
@EnableBinding(PanChannels.class)
@Log4j2
public class MPanServerLauncher {
    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(MPanServerLauncher.class, args);
        printStartLog(app);
    }

    private static void printStartLog(ConfigurableApplicationContext app) {
        String port = app.getEnvironment().getProperty("server.port");
        String url = "http://localhost:" + port;
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, url));
        if (enableSwagger2(app)) {
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, url + "/doc.html"));
        }
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "M-Pan started successfully!"));
    }

    private static boolean enableSwagger2(ConfigurableApplicationContext applicationContext) {
        return applicationContext.getEnvironment().getProperty("swagger2.enable", Boolean.class, true)
                && applicationContext.containsBean("swagger2Config");
    }


}
