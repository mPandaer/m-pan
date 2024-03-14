package com.pandaer.pan.bloom.filter.core;

/**
 * 布隆过滤器的顶级接口
 */
public interface BloomFilter<T> {

    /**
     * 放入数据
     * @param object
     * @return
     */
    boolean put(T object);

    /**
     * 判断数据是否存在
     * @param object
     * @return
     */
    boolean mightContain(T object);

    /**
     * 清空布隆过滤器
     */
    void clear();

}
