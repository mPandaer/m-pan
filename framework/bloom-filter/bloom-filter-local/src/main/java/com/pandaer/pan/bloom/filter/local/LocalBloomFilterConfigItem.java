package com.pandaer.pan.bloom.filter.local;

import lombok.Data;

@Data
public class LocalBloomFilterConfigItem {

    /**
     * 布隆过滤器的名称
     */
    private String name;

    /**
     * 通道的名称
     */
    private String funnelTypeName = FunnelType.LONG.name();

    /**
     * 数组的大小
     */
    private long expectedInsertions = 1000000L;

    /**
     * 误判率
     */
    private double fpp = 0.01D;

}
