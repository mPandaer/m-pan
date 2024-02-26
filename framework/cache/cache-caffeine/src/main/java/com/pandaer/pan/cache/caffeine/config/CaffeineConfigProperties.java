package com.pandaer.pan.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "pan.cache.caffeine")
public class CaffeineConfigProperties {

    private Integer initCacheCapacity = 100;

    private Long maxCacheCapacity = 10000L;

    private Boolean allowNullValues = Boolean.TRUE;
}
