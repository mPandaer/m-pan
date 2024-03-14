package com.pandaer.pan.server.common.task;


import cn.hutool.core.collection.CollectionUtil;
import com.pandaer.pan.bloom.filter.core.BloomFilter;
import com.pandaer.pan.bloom.filter.core.BloomFilterManager;
import com.pandaer.pan.schedule.ScheduleTask;
import com.pandaer.pan.server.modules.share.constants.ShareConstants;
import com.pandaer.pan.server.modules.share.service.IShareService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * 重建布隆过滤器定时任务
 */
@Component
@Log4j2
public class RebuildShareSimpleBloomFilterTask implements ScheduleTask {

    @Autowired
    private BloomFilterManager manager;

    @Autowired
    private IShareService shareService;


    @Override
    public String getName() {
        return "rebuildShareSimpleBloomFilterTask";
    }

    @Override
    public void run() {
        log.info("{} rebuild start...",getName());
        BloomFilter<Long> filter = manager.getFilter(ShareConstants.SHARE_SIMPLE_BLOOM_FILTER_NAME);
        if (Objects.isNull(filter)) {
            log.warn("init {} 不存在",getName());
            return;
        }
        filter.clear();
        Long startId = 0L;
        Long limit = 10000L;
        List<Long> shareIdList;
        do {
            shareIdList = shareService.rollingGetShareId(startId,limit);
            for (Long shareId : shareIdList) {
                filter.put(shareId);
            }
            startId = Collections.max(shareIdList);
        }while (CollectionUtil.isNotEmpty(shareIdList));
        log.info("{} rebuild finish...",getName());
    }


}
