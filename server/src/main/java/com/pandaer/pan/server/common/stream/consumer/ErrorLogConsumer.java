package com.pandaer.pan.server.common.stream.consumer;


import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.log.ErrorLogEvent;
import com.pandaer.pan.server.modules.log.domain.MPanErrorLog;
import com.pandaer.pan.server.modules.log.service.IErrorLogService;
import com.pandaer.pan.stream.core.AbstractConsumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@Log4j2
public class ErrorLogConsumer extends AbstractConsumer {

    @Autowired
    private IErrorLogService errorLogService;

    @StreamListener(PanChannels.ERROR_LOG_INPUT)
    @Async("eventListenerTaskExecutor")
    public void listenErrorLogEvent(Message<ErrorLogEvent> message) {
        if (Objects.isNull(message)) {
            return;
        }
        printLog(message);
        ErrorLogEvent errorLogEvent = message.getPayload();
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
