package com.pandaer.pan.server.common.listener;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.event.file.SearchFileEvent;
import com.pandaer.pan.server.modules.user.domain.MPanUserSearchHistory;
import com.pandaer.pan.server.modules.user.service.IUserSearchHistoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class FileEventListener {

    @Autowired
    private IUserSearchHistoryService searchHistoryService;

    @EventListener(SearchFileEvent.class)
    public void listenSearchFileEvent(SearchFileEvent event) {
        log.info("searchFileEvent:{}", event);
        MPanUserSearchHistory history = new MPanUserSearchHistory();
        history.setId(IdUtil.get());
        history.setUserId(event.getUserId());
        history.setSearchContent(event.getKeyword());
        history.setCreateTime(new Date());
        history.setUpdateTime(new Date());

        try {
            searchHistoryService.save(history);
        } catch (DuplicateKeyException e) {
            LambdaUpdateWrapper<MPanUserSearchHistory> update = new LambdaUpdateWrapper<>();
            update.eq(MPanUserSearchHistory::getUserId, event.getUserId());
            update.eq(MPanUserSearchHistory::getSearchContent,event.getKeyword());
            update.set(MPanUserSearchHistory::getUpdateTime,new Date());
            searchHistoryService.update(update);
        }
    }
}
