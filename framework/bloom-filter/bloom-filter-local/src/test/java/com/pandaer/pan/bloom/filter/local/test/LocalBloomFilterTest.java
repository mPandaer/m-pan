package com.pandaer.pan.bloom.filter.local.test;

import com.pandaer.pan.bloom.filter.core.BloomFilter;
import com.pandaer.pan.bloom.filter.local.LocalBloomFilterManager;
import com.pandaer.pan.core.constants.MPanConstants;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = MPanConstants.BASE_COMPONENT_PACKAGE_PATH + ".bloom.filter.local")
@Log4j2
public class LocalBloomFilterTest {

    @Autowired
    private LocalBloomFilterManager manager;

    @Test
    public void testLocalBloomFilter() {
        BloomFilter<Object> bloomFilter = manager.getFilter("test");
        for (int i = 0; i < 100000; i++) {
            bloomFilter.put(i);
        }
        int failNum = 0;
        for (int i = 0; i <10000; i++) {
            boolean res = bloomFilter.mightContain(100000 + i);
            if (res) {
                failNum++;
            }
        }
        log.info("一万次非法数据测试：误判数为 {}",failNum);

    }
}
