package com.pandaer.pan.server.common.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.schedule.ScheduleTask;
import com.pandaer.pan.server.common.event.log.ErrorLogEvent;
import com.pandaer.pan.server.modules.file.domain.MPanFileChunk;
import com.pandaer.pan.server.modules.file.service.IFileChunkService;
import com.pandaer.pan.storage.engine.core.StorageEngine;
import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清理过期的分块文件滚动任务
 * 业务逻辑
 * 1. 滚动查询过期的分片文件记录
 * 2. 删除过期的物理的分片文件
 * 3. 删除过期的分片文件记录
 * 4. 重置索引id,并继续滚动查询
 */
@Component
@Log4j2
public class CleanExpireChunkFileTask implements ScheduleTask, ApplicationContextAware {

    public static final Long BATCH_SIZE = 500L;

    @Autowired
    private IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public String getName() {
        return "CleanExpireChunkFileTask";
    }

    @Override
    public void run() {
        log.info("开始清理过期分片文件");
        List<MPanFileChunk> fileCHunkRecords;
        Long scrollId = 0L;
        do {
            fileCHunkRecords = scrollQueryExpireChunkFile(scrollId);
            if (fileCHunkRecords.isEmpty()) {
                break;
            }
            deleteRealChunkFile(fileCHunkRecords);
            List<Long> idList = deleteChunkFileRecord(fileCHunkRecords);
            scrollId = Collections.max(idList);
        } while (CollectionUtil.isNotEmpty(fileCHunkRecords));

        log.info("清理过期分片文件结束");
    }

    private List<Long> deleteChunkFileRecord(List<MPanFileChunk> fileCHunkRecords) {
        List<Long> idList = fileCHunkRecords.stream().map(MPanFileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(idList);
        return idList;
    }

    private void deleteRealChunkFile(List<MPanFileChunk> fileCHunkRecords) {
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<String> pathList = fileCHunkRecords.stream().map(MPanFileChunk::getRealPath).collect(Collectors.toList());
        deleteFileContext.setRealFilePathList(pathList);
        try {
            storageEngine.deleteFile(deleteFileContext);
        } catch (IOException e) {
            ErrorLogEvent event = new ErrorLogEvent(this,"分片文件删除失败 分片路径：" + JSON.toJSONString(pathList), MPanConstants.ZERO_LONG);
            applicationContext.publishEvent(event);
        }
    }


    /**
     * 滚动查询过期的分片文件记录
     * @param scrollId
     * @return
     */
    private List<MPanFileChunk> scrollQueryExpireChunkFile(Long scrollId) {
        LambdaQueryWrapper<MPanFileChunk> query = new LambdaQueryWrapper<>();
        query.le(MPanFileChunk::getExpirationTime,new Date())
                .ge(MPanFileChunk::getId,scrollId);
        query.last("limit " + BATCH_SIZE);
        return fileChunkService.list(query);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
