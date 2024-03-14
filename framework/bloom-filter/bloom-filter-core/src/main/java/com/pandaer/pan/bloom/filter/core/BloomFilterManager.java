package com.pandaer.pan.bloom.filter.core;

import java.util.List;

/**
 * 布隆过滤器管理器
 */
public interface BloomFilterManager {

    /**
     * 通过名称获取布隆过滤器
     * @param name
     * @return
     */
    <T> BloomFilter<T> getFilter(String name);

    /**
     * 获取该Manager管理的布隆过滤器名称
     * @return
     */
    List<String> getFilterNames();
}
