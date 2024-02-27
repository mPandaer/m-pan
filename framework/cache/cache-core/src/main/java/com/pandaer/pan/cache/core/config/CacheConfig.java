package com.pandaer.pan.cache.core.config;

import com.pandaer.pan.cache.core.constants.CacheConstants;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class CacheConfig {


    @ConditionalOnBean(CacheManager.class)
    @Bean
    public Cache panCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache(CacheConstants.M_PAN_CACHE_NAME);
        if (cache == null) {
            throw new MPanFrameworkException("获取默认缓存失败");
        }
        return cache;
    }
}
