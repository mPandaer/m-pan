package com.pandaer.pan.storage.engine.local.initializer;


import com.pandaer.pan.storage.engine.local.config.LocalStorageEngineConfigProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * 文件资源初始化器
 * 主要功能
 * 1. 初始化默认的上传文件目录以及分块文件目录
 */
@Component
@Log4j2
public class FileResourceInitializer implements CommandLineRunner {

    @Autowired
    private LocalStorageEngineConfigProperties properties;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(properties.getBasePath()));
        log.info("文件资源初始化成功，文件上传目录：{}", properties.getBasePath());
        FileUtils.forceMkdir(new File(properties.getChunkBasePath()));
        log.info("文件资源初始化成功，文件分块目录：{}", properties.getChunkBasePath());
    }
}
