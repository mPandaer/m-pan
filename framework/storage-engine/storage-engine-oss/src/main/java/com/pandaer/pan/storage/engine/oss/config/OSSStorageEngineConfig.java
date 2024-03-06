package com.pandaer.pan.storage.engine.oss.config;

import com.aliyun.oss.OSSClient;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * OSS存储引擎相关配置
 */
@SpringBootConfiguration
@Log4j2
@ConfigurationProperties(prefix = "pan.storage.engine.oss")
@Data
public class OSSStorageEngineConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private Boolean autoCreateBucket = Boolean.TRUE;


    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient() {
        if (StringUtils.isAnyBlank(endpoint, accessKeyId, accessKeySecret, bucketName)) {
            log.error("OSS存储引擎配置不完整");
            throw new MPanFrameworkException("OSS存储引擎配置不完整");
        }
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

}
