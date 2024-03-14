package com.pandaer.pan.server.modules.share.service.share;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.pan.server.common.cache.AbstractManualCacheService;
import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.mapper.MPanShareMapper;
import com.pandaer.pan.server.modules.share.service.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShareCacheService extends AbstractManualCacheService<MPanShare> {

    @Autowired
    private MPanShareMapper shareMapper;

    @Override
    protected BaseMapper<MPanShare> getBaseMapper() {
        return shareMapper;
    }

    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
