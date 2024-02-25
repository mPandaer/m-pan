package com.pandaer.pan.server;

import com.pandaer.pan.core.constants.MPanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH)
@ServletComponentScan(basePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH + ".server.modules.**.mapper")
public class MPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(MPanServerLauncher.class, args);
    }

}
