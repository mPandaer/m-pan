package com.pandaer.pan.server.common.listener;


import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.event.log.ErrorLogEvent;
import com.pandaer.pan.server.modules.log.domain.MPanErrorLog;
import com.pandaer.pan.server.modules.log.service.IErrorLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class ErrorLogListener {

    @Autowired
    private IErrorLogService errorLogService;

    @EventListener(ErrorLogEvent.class)
    @Async("eventListenerTaskExecutor")
    public void listenErrorLogEvent(ErrorLogEvent errorLogEvent) {
        MPanErrorLog logRecord = new MPanErrorLog();
        logRecord.setId(IdUtil.get());
        logRecord.setLogContent(errorLogEvent.getErrMsg());
        logRecord.setLogStatus(0);
        logRecord.setCreateTime(new Date());
        logRecord.setCreateUser(errorLogEvent.getUserId());
        logRecord.setUpdateTime(new Date());
        logRecord.setUpdateUser(errorLogEvent.getUserId());
        if (!errorLogService.save(logRecord)) {
            throw new MPanFrameworkException("保存错误日志失败,请联系管理员");
        }
    }
}
