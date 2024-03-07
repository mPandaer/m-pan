package com.pandaer.pan.storage.engine.oss.initializer;


import com.aliyun.oss.OSSClient;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.storage.engine.oss.config.OSSStorageEngineConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * OSS bucket初始化器
 * 主要功能
 * 1. 根据用户配置初始化OSS bucket
 */
@Component
@Log4j2
public class OSSBucketInitializer implements CommandLineRunner {

    @Autowired
    private OSSStorageEngineConfig config;

    @Autowired
    private OSSClient ossClient;


    @Override
    public void run(String... args) throws Exception {
        if (StringUtils.isBlank(config.getBucketName())) {
            throw new MPanFrameworkException("OSS BucketName未配置");
        }
        boolean exist = ossClient.doesBucketExist(config.getBucketName());
        if (!exist && config.getAutoCreateBucket()) {
            ossClient.createBucket(config.getBucketName());
            log.info("OSS Bucket初始化成功，BucketName：{}", config.getBucketName());
            return;
        }
        if (!exist) {
            log.error("OSS Bucket不存在，BucketName：{}", config.getBucketName());
            throw new MPanFrameworkException("OSS Bucket不存在，BucketName：" + config.getBucketName());
        }

    }
}
