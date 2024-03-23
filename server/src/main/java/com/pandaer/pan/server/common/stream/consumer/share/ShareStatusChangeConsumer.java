package com.pandaer.pan.server.common.stream.consumer.share;

import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.file.DeleteFileWithRecycleEvent;
import com.pandaer.pan.server.common.stream.event.file.RestoreFileEvent;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.share.service.IShareService;
import com.pandaer.pan.stream.core.AbstractConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 监听文件状态变化导致分享状态变化的处理器
 */
@Component
public class ShareStatusChangeConsumer extends AbstractConsumer {

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IShareService shareService;

    /**
     * 处理分享的文件被删除，导致分享状态标记为有文件被删除状态
     * 具体处理逻辑
     * 1. 获取被删除的文件列表
     * 2. 通过文件列表获取到全部被删除的文件
     * 3. 根据这些文件去刷新分享状态
     */
    @StreamListener(PanChannels.DELETE_FILE_INPUT)
    @Async("eventListenerTaskExecutor")
    public void handleDeleteFileWithRecycleEvent(Message<DeleteFileWithRecycleEvent> message) {
        if (Objects.isNull(message)) {
            return;
        }
        printLog(message);
        //这次删除的文件列表
        DeleteFileWithRecycleEvent event = message.getPayload();
        List<Long> deleteFileIdList = event.getDeleteFileIdList();
        List<MPanUserFile> initDeleteFileList = userFileService.listByIds(deleteFileIdList);
        //获取到全部的文件 不管是否删除
        List<MPanUserFile> allDeleteFileList = userFileService.findAllRecords(initDeleteFileList);
        shareService.refreshShareStatus(allDeleteFileList);
    }


    /**
     * 处理分享的文件被还原，导致分享状态有可能变为正常状态
     */
    @StreamListener(PanChannels.FILE_RESTORE_INPUT)
    @Async("eventListenerTaskExecutor")
    public void handleRestoreFileEvent(Message<RestoreFileEvent> message) {
        if (Objects.isNull(message)) {
            return;
        }
        printLog(message);
        //这次还原的文件列表
        RestoreFileEvent event = message.getPayload();
        List<Long> restoreFileIdList = event.getRestoreFileIdList();
        List<MPanUserFile> initDeleteFileList = userFileService.listByIds(restoreFileIdList);
        //获取到全部的文件 不管是否删除
        List<MPanUserFile> allDeleteFileList = userFileService.findAllRecords(initDeleteFileList);
        shareService.refreshShareStatus(allDeleteFileList);
    }

}
