//package com.pandaer.pan.server.common.listener;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.pandaer.pan.core.constants.MPanConstants;
//import com.pandaer.pan.core.utils.IdUtil;
//import com.pandaer.pan.server.common.event.file.ActualDeleteFileEvent;
//import com.pandaer.pan.server.common.event.file.SearchFileEvent;
//import com.pandaer.pan.server.common.event.log.ErrorLogEvent;
//import com.pandaer.pan.server.modules.file.constants.FileConstants;
//import com.pandaer.pan.server.modules.file.domain.MPanFile;
//import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
//import com.pandaer.pan.server.modules.file.service.IFileService;
//import com.pandaer.pan.server.modules.file.service.IUserFileService;
//import com.pandaer.pan.server.modules.user.domain.MPanUserSearchHistory;
//import com.pandaer.pan.server.modules.user.service.IUserSearchHistoryService;
//import com.pandaer.pan.storage.engine.core.StorageEngine;
//import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.event.EventListener;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Component
//@Log4j2
//public class FileEventListener implements ApplicationContextAware {
//
//    @Autowired
//    private IUserSearchHistoryService searchHistoryService;
//
//    @Autowired
//    private StorageEngine storageEngine;
//
//    @Autowired
//    private IFileService fileService;
//
//    @Autowired
//    private IUserFileService userFileService;
//
//    private ApplicationContext applicationContext;
//
//    /**
//     * 监听搜索文件事件并保存搜索历史
//     * @param event
//     */
//    @EventListener(SearchFileEvent.class)
//    @Async("eventListenerTaskExecutor")
//    public void listenSearchFileEvent(SearchFileEvent event) {
//        log.info("searchFileEvent:{}", event);
//        MPanUserSearchHistory history = new MPanUserSearchHistory();
//        history.setId(IdUtil.get());
//        history.setUserId(event.getUserId());
//        history.setSearchContent(event.getKeyword());
//        history.setCreateTime(new Date());
//        history.setUpdateTime(new Date());
//
//        try {
//            searchHistoryService.save(history);
//        } catch (DuplicateKeyException e) {
//            LambdaUpdateWrapper<MPanUserSearchHistory> update = new LambdaUpdateWrapper<>();
//            update.eq(MPanUserSearchHistory::getUserId, event.getUserId());
//            update.eq(MPanUserSearchHistory::getSearchContent,event.getKeyword());
//            update.set(MPanUserSearchHistory::getUpdateTime,new Date());
//            searchHistoryService.update(update);
//        }
//    }
//
//    /**
//     * 监听文件删除事件并删除实际物理文件
//     * 1. 查找无引用的文件记录
//     * 2. 删除物理文件
//     * 3. 删除物理文件记录
//     */
//    @EventListener(ActualDeleteFileEvent.class)
//    @Async("eventListenerTaskExecutor")
//    public void listenActualDeleteFileEvent(ActualDeleteFileEvent event) {
//        List<MPanUserFile> allRecords = event.getAllRecords();
//        if (allRecords.isEmpty()) {
//            return;
//        }
//        List<Long> realFileIdList = allRecords.stream()
//                .filter(record -> Objects.equals(record.getFolderFlag(), FileConstants.NO))
//                .filter(this::unUsed)
//                .map(MPanUserFile::getRealFileId)
//                .collect(Collectors.toList());
//        List<MPanFile> realFileList = fileService.listByIds(realFileIdList);
//        deleteRealFile(realFileList);
//        if (!fileService.removeByIds(realFileIdList)) {
//            log.error("删除文件记录失败");
//            ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "删除文件记录失败" + realFileIdList, MPanConstants.ZERO_LONG);
//            applicationContext.publishEvent(errorLogEvent);
//        }
//
//    }
//
//    private void deleteRealFile(List<MPanFile> realFileList) {
//        List<String> realPathList = realFileList.stream().map(MPanFile::getRealPath).collect(Collectors.toList());
//        DeleteFileContext deleteFileContext = new DeleteFileContext();
//        deleteFileContext.setRealFilePathList(realPathList);
//        try {
//            storageEngine.deleteFile(deleteFileContext);
//        } catch (IOException e) {
//            log.error("删除物理失败");
//            ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "删除物理文件失败" + realPathList, MPanConstants.ZERO_LONG);
//            applicationContext.publishEvent(errorLogEvent);
//        }
//    }
//
//    private boolean unUsed(MPanUserFile mPanUserFile) {
//        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
//        query.eq(MPanUserFile::getRealFileId, mPanUserFile.getRealFileId());
//        return userFileService.count(query) == 0;
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}
