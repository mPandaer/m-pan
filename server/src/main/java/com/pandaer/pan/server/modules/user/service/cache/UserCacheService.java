package com.pandaer.pan.server.modules.user.service.cache;

import com.pandaer.pan.cache.core.constants.CacheConstants;
import com.pandaer.pan.server.common.cache.AnnotationCacheService;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.mapper.MPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UserCacheService implements AnnotationCacheService<MPanUser> {

    @Autowired
    private MPanUserMapper userMapper;

    @Cacheable(cacheNames = CacheConstants.M_PAN_CACHE_NAME,keyGenerator = "userIdKeyGenerator",sync = true)
    @Override
    public MPanUser getById(Serializable id) {
        return userMapper.selectById(id);
    }

    @Override
    @CachePut(cacheNames = CacheConstants.M_PAN_CACHE_NAME,keyGenerator = "userIdKeyGenerator")
    public boolean updateById(Serializable id, MPanUser entity) {
        return userMapper.updateById(entity) > 0;
    }

    @CacheEvict(cacheNames = CacheConstants.M_PAN_CACHE_NAME,keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return userMapper.deleteById(id) > 0;
    }
}
