package com.pandaer.pan.cache.caffeine.test;

import com.pandaer.pan.cache.caffeine.test.config.CaffeineCacheConfig;
import com.pandaer.pan.cache.caffeine.test.instance.AnnotationTester;
import com.pandaer.pan.cache.core.constants.CacheConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CaffeineCacheConfig.class)
public class CaffeineTest {

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
