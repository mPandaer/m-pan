//package com.pandaer.pan.server.common.listener.share;
//
//import com.pandaer.pan.server.common.event.file.DeleteFileWithRecycleEvent;
//import com.pandaer.pan.server.common.event.file.RestoreFileEvent;
//import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
//import com.pandaer.pan.server.modules.file.service.IUserFileService;
//import com.pandaer.pan.server.modules.share.service.IShareService;
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 监听文件状态变化导致分享状态变化的处理器
// */
//@Component
//public class ShareStatusChangeListener {
//
//    @Autowired
//    private IUserFileService userFileService;
//
//    @Autowired
//    private IShareService shareService;
//
//    /**
//     * 处理分享的文件被删除，导致分享状态标记为有文件被删除状态
//     * 具体处理逻辑
//     * 1. 获取被删除的文件列表
//     * 2. 通过文件列表获取到全部被删除的文件
//     * 3. 根据这些文件去刷新分享状态
//     * @param event
//     */
//    @EventListener(DeleteFileWithRecycleEvent.class)
//    @Async("eventListenerTaskExecutor")
//    public void handleDeleteFileWithRecycleEvent(DeleteFileWithRecycleEvent event) {
//        //这次删除的文件列表
//        List<Long> deleteFileIdList = event.getDeleteFileIdList();
//        List<MPanUserFile> initDeleteFileList = userFileService.listByIds(deleteFileIdList);
//        //获取到全部的文件 不管是否删除
//        List<MPanUserFile> allDeleteFileList = userFileService.findAllRecords(initDeleteFileList);
//        shareService.refreshShareStatus(allDeleteFileList);
//    }
//
//
//    /**
//     * 处理分享的文件被还原，导致分享状态有可能变为正常状态
//     * @param event
//     */
//    @EventListener(RestoreFileEvent.class)
//    @Async("eventListenerTaskExecutor")
//    public void handleRestoreFileEvent(RestoreFileEvent event) {
//        //这次还原的文件列表
//        List<Long> restoreFileIdList = event.getRestoreFileIdList();
//        List<MPanUserFile> initDeleteFileList = userFileService.listByIds(restoreFileIdList);
//        //获取到全部的文件 不管是否删除
//        List<MPanUserFile> allDeleteFileList = userFileService.findAllRecords(initDeleteFileList);
//        shareService.refreshShareStatus(allDeleteFileList);
//    }
//
//}
