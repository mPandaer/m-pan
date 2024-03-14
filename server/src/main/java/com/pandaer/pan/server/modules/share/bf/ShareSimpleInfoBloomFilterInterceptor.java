package com.pandaer.pan.server.modules.share.bf;

import cn.hutool.core.collection.CollectionUtil;
import com.pandaer.pan.bloom.filter.core.BloomFilter;
import com.pandaer.pan.bloom.filter.core.BloomFilterManager;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.interceptor.BloomFilterInterceptor;
import com.pandaer.pan.server.modules.share.constants.ShareConstants;
import com.pandaer.pan.server.modules.share.service.IShareService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 获取简单分享详情的布隆过滤器
 */
@Component
@Data
@Log4j2
public class ShareSimpleInfoBloomFilterInterceptor implements BloomFilterInterceptor{

    @Autowired
    private BloomFilterManager manager;

    @Autowired
    private IShareService shareService;


    @Override
    public String getName() {
        return "shareSimpleInfoBloomFilterInterceptor";
    }

    @Override
    public String[] patterns() {
        return new String[]{"/share/simple"};
    }

    @Override
    public String[] excludePatterns() {
        return new String[0];
    }

    /**
     * 初始化布隆过滤器
     * 1. 在容器启动时，就要将相关数据放入布隆过滤器中
     */
    @Override
    public void initBloomFilter() {
        log.info("{} init start...",getName());
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
            if (CollectionUtil.isNotEmpty(shareIdList)) {
                for (Long shareId : shareIdList) {
                    filter.put(shareId);
                }
                startId = Collections.max(shareIdList);
            }
        }while (CollectionUtil.isNotEmpty(shareIdList));
        log.info("{} init finish...",getName());
    }

    /**
     * 定时重建布隆过滤器
     */
    @Override
    public void rebuildBloomFilter() {

    }

    @Override
    public BloomFilter<Object> getFilter() {
        BloomFilter<Object> filter = manager.getFilter(ShareConstants.SHARE_SIMPLE_BLOOM_FILTER_NAME);
        if (Objects.isNull(filter)) {
            throw new MPanFrameworkException("布隆过滤器未配置");
        }
        return filter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BloomFilter<Object> filter = manager.getFilter(ShareConstants.SHARE_SIMPLE_BLOOM_FILTER_NAME);
        if (Objects.isNull(filter)) {
            log.info("未找到布隆过滤器 ({})", request.getRequestURI());
            return true;
        }
        String encShareId = request.getParameter("shareId");
        if (StringUtils.isBlank(encShareId)) {
            return true;
        }
        if (filter.mightContain(IdUtil.decrypt(encShareId))) {
            log.info("布隆过滤器[{}]验证({})通过 shareId: {}",getName(), request.getRequestURI(), IdUtil.decrypt(encShareId));
            return true;
        }
        throw new MPanBusinessException("非法数据");
    }


}
