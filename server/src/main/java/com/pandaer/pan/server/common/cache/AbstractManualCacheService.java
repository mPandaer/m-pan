package com.pandaer.pan.server.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.Lists;
import com.pandaer.pan.cache.core.constants.CacheConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 手动缓存处理的公用顶级父类
 * @param <V>
 */
public abstract class AbstractManualCacheService<V> implements ManualCacheService<V> {


    @Autowired(required = false)
    private CacheManager cacheManager;

    private final Object lock = new Object();

    protected abstract BaseMapper<V> getBaseMapper();

    /**
     * 根据ID查询实体
     * 1. 查询缓存，如果命中，就直接返回
     * 2. 没有命中，就去数据库查，查到数据就返回并加入缓存中
     * @param id
     * @return
     */
    @Override
    public V getById(Serializable id) {
        V result = getByCache(id);
        if (Objects.nonNull(result)) {
            return result;
        }

        /**
         * 使用锁机制避免缓存击穿的问题（大量请求同时访问数据库）
         */
        synchronized (lock) {
            result = getByCache(id);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = getByDB(id);
            if (Objects.nonNull(result)) {
                putCache(id,result);
            }

        }
        return result;
    }

    /**
     * 将实体信息保存到缓存中
     * @param id
     * @param entity
     */
    private void putCache(Serializable id, V entity) {
        if (Objects.isNull(entity)) {
            return;
        }
        String cacheKey = genCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return;
        }
        cache.put(cacheKey,entity);
    }

    /**
     * 通过主键从数据库中查询数据
     * @param id
     * @return
     */
    private V getByDB(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     *从缓存中获取数据
     * @param id
     * @return
     */
    private V getByCache(Serializable id) {
        String cacheKey = genCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (Objects.isNull(valueWrapper)) {
            return null;
        }
        return (V) valueWrapper.get();
    }

    /**
     * 根据ID生成key
     * @param id
     * @return
     */
    private String genCacheKey(Serializable id) {
        return String.format(getKeyFormat(),id);
    }

    /**
     * 更新缓存
     * @param id
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(Serializable id, V entity) {
        int res = getBaseMapper().updateById(entity);
        removeByCache(id);
        return res > 0;
    }

    /**
     * 移除缓存
     * @param id
     */
    private void removeByCache(Serializable id) {
        String cacheKey = genCacheKey(id);
        if (Objects.isNull(cacheKey)) {
            return;
        }
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return;
        }
        cache.evict(cacheKey);
    }

    @Override
    public boolean removeById(Serializable id) {
        int res = getBaseMapper().deleteById(id);
        removeByCache(id);
        return res > 0;
    }

    @Override
    public List<V> getByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return ids.stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public boolean updateByIds(Map<? extends Serializable, V> entityMap) {
        if (MapUtil.isEmpty(entityMap)) {
            return false;
        }
        Set<? extends Map.Entry<? extends Serializable, V>> entries = entityMap.entrySet();
        for (Map.Entry<? extends Serializable, V> entry : entries) {
            if (!updateById(entry.getKey(),entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        for (Serializable id : ids) {
            if (!removeById(id)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new MPanBusinessException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.M_PAN_CACHE_NAME);
    }
}
