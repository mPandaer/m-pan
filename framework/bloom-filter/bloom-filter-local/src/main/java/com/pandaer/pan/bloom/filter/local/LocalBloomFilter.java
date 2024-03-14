package com.pandaer.pan.bloom.filter.local;

import com.google.common.hash.Funnel;
import com.pandaer.pan.bloom.filter.core.BloomFilter;
import lombok.Data;

/**
 * 本地布隆过滤器
 * @param <T>
 */

@Data
public class LocalBloomFilter<T> implements BloomFilter<T> {
    private Funnel<T> funnel;

    /**
     * 数组的大小
     */
    private long expectedInsertions;

    /**
     * 误判率
     */
    private double fpp;

    private com.google.common.hash.BloomFilter<T> delegate;

    public LocalBloomFilter(Funnel<T> funnel, long expectedInsertions, double fpp) {
        this.funnel = funnel;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.delegate = com.google.common.hash.BloomFilter.create(funnel,expectedInsertions,fpp);
    }

    @Override
    public boolean put(T object) {
        return delegate.put(object);
    }

    @Override
    public boolean mightContain(T object) {
        return delegate.mightContain(object);
    }

    @Override
    public void clear() {
        this.delegate = com.google.common.hash.BloomFilter.create(funnel,expectedInsertions,fpp);
    }
}
