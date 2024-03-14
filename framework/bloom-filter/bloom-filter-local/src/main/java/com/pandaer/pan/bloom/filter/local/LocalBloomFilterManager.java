package com.pandaer.pan.bloom.filter.local;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.pandaer.pan.bloom.filter.core.BloomFilter;
import com.pandaer.pan.bloom.filter.core.BloomFilterManager;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
public class LocalBloomFilterManager implements BloomFilterManager, InitializingBean {

    @Autowired
    private LocalBloomFilterConfig config;

    private Map<String,LocalBloomFilter> localBloomFilterContainer = Maps.newConcurrentMap();

    @Override
    public <T> BloomFilter<T> getFilter(String name) {
        return localBloomFilterContainer.get(name);
    }

    @Override
    public List<String> getFilterNames() {
        return new ArrayList<>(localBloomFilterContainer.keySet());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if (CollectionUtil.isEmpty(items)) {
            throw new MPanFrameworkException("没有配置布隆过滤器列表");
        }
        items.forEach(item -> {
            String funnelTypeName = item.getFunnelTypeName();
            try {
                FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                localBloomFilterContainer.put(item.getName(),new LocalBloomFilter(funnelType.getFunnel(),item.getExpectedInsertions(),item.getFpp()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("配置的Funnel类型不支持" + funnelTypeName);
            }
        });
    }
}
