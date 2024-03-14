package com.pandaer.pan.server.common.interceptor;

import com.pandaer.pan.bloom.filter.core.BloomFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 布隆过滤器 顶级拦截器
 */
public interface BloomFilterInterceptor<T> extends HandlerInterceptor,CommandLineRunner {

    String getName();

    String[] patterns();

    String[] excludePatterns();

    void initBloomFilter();

    void rebuildBloomFilter();

    BloomFilter<T> getFilter();

    @Override
    default void run(String... args) throws Exception {
        initBloomFilter();
    }
}
