package com.pandaer.pan.cache.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pandaer.pan.cache.core.constants.CacheConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableCaching
@Log4j2
public class CaffeineCacheConfig {

    @Autowired
    private CaffeineConfigProperties caffeineConfigProperties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(CacheConstants.M_PAN_CACHE_NAME);
        caffeineCacheManager.setAllowNullValues(caffeineConfigProperties.getAllowNullValues());
        //Cache Build
        Caffeine<Object,Object> caffeine = Caffeine.newBuilder()
                .initialCapacity(caffeineConfigProperties.getInitCacheCapacity())
                .maximumSize(caffeineConfigProperties.getMaxCacheCapacity());
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
