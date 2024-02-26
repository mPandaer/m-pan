package com.pandaer.pan.cache.caffeine.test.instance;

import com.pandaer.pan.cache.core.constants.CacheConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AnnotationTester {

    @Cacheable(cacheNames = CacheConstants.M_PAN_CACHE_NAME,key = "#name")
    public String testCacheable(String name) {
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW,"cacheable success ",name));
        log.info("heihei");
        return "hello " + name;
    }
}
