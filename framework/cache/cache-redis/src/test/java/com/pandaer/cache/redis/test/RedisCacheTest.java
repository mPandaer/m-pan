package com.pandaer.cache.redis.test;

import com.pandaer.cache.redis.test.config.RedisCacheConfig;
import com.pandaer.cache.redis.test.instance.AnnotationTester;
import com.pandaer.pan.cache.core.constants.CacheConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@SpringBootTest(classes = RedisCacheConfig.class)
@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AnnotationTester annotationTester;


    @Test
    public void testCacheManager() {
        Cache cache = cacheManager.getCache(CacheConstants.M_PAN_CACHE_NAME);
        Assert.assertNotNull(cache);
        cache.put("name","value");
        String value = cache.get("name", String.class);
        Assert.assertEquals("value",value);
    }

    @Test
    public void testAnnotation() {
        for (int i = 0; i < 2; i++) {
            annotationTester.testCacheable("pandaer");
        }
    }
}
