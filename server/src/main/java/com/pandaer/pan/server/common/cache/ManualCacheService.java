package com.pandaer.pan.server.common.cache;

import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 手动缓存处理顶级接口
 * @param <V>
 */
public interface ManualCacheService<V> extends CacheService<V> {

    List<V> getByIds(Collection<? extends Serializable> ids);

    boolean updateByIds(Map<?extends Serializable,V> entityMap);

    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 获取缓存Key的模板信息
     * @return
     */
    String getKeyFormat();

    /**
     * 获取缓存对象实体
     * @return
     */
    Cache getCache();
}
