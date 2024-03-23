package com.pandaer.pan.server.modules.recycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.file.ActualDeleteFileEvent;
import com.pandaer.pan.server.common.stream.event.file.RestoreFileEvent;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.ActualDeleteFileContext;
import com.pandaer.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;
import com.pandaer.pan.server.modules.recycle.service.IRecycleService;
import com.pandaer.pan.stream.core.IStreamProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecycleServiceImpl implements IRecycleService {

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    @Qualifier("defaultStreamProducer")
    private IStreamProducer streamProducer;


    @Override
    public List<UserFileVO> queryRecycleFileList(QueryRecycleFileListContext queryRecycleFileListContext) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(queryRecycleFileListContext.getUserId());
        queryFileListContext.setDelFlag(FileConstants.YES);
        return iUserFileService.getFileList(queryFileListContext);
    }

    /**
     * 批量还原文件
     * 功能实现
     * 1. 检查操作权限以及文件的合法性
     * 2. 检查文件是否可以还原
     * 3. 修改文件记录的删除标志
     * 4. 执行还原的后置操作
     *
     * @param restoreFileContext
     */
    @Override
    public void restore(RestoreFileContext restoreFileContext) {
        checkRestorePermission(restoreFileContext);
        checkRestoreFilename(restoreFileContext);
        doRestore(restoreFileContext);
        AfterRestore(restoreFileContext);
    }

    /**
     * 批量彻底删除文件
     * 功能实现
     * 1. 检查操作权限以及文件的合法性
     * 2. 查找的需要删除的全部文件列表
     * 3. 删除物理文件，以及文件记录
     * 4. 发布文件彻底删除事件
     *
     * @param actualDeleteFileContext
     */
    @Override
    public void actualDelete(ActualDeleteFileContext actualDeleteFileContext) {
        checkDeleteFilePermission(actualDeleteFileContext);
        findAllRecords(actualDeleteFileContext);
        doDelete(actualDeleteFileContext);
        AfterDelete(actualDeleteFileContext);
    }

    /**
     * 文件删除后的操作
     * 1. 发布文件彻底删除事件
     * @param actualDeleteFileContext
     */
    private void AfterDelete(ActualDeleteFileContext actualDeleteFileContext) {
        ActualDeleteFileEvent event = new ActualDeleteFileEvent(actualDeleteFileContext.getAllRecords());
        streamProducer.sendMessage(PanChannels.PHYSICAL_DELETE_FILE_OUTPUT, event);
    }

    /**
     * 执行删除的动作
     * @param actualDeleteFileContext
     */
    private void doDelete(ActualDeleteFileContext actualDeleteFileContext) {
        List<MPanUserFile> allRecords = actualDeleteFileContext.getAllRecords();
        iUserFileService.removeByIds(allRecords.stream().map(MPanUserFile::getFileId).collect(Collectors.toList()));
    }


    /**
     * 递归查找所有的要删除的文件记录
     * @param actualDeleteFileContext
     */
    private void findAllRecords(ActualDeleteFileContext actualDeleteFileContext) {
        List<MPanUserFile> nestRecords = actualDeleteFileContext.getNestRecords();
        List<MPanUserFile> allRecords = iUserFileService.findAllRecords(nestRecords);
        actualDeleteFileContext.setAllRecords(allRecords);
    }

    private void checkDeleteFilePermission(ActualDeleteFileContext actualDeleteFileContext) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId, actualDeleteFileContext.getUserId())
                .eq(MPanUserFile::getDelFlag, FileConstants.YES)
                .in(MPanUserFile::getFileId, actualDeleteFileContext.getFileIdList());
        List<MPanUserFile> list = iUserFileService.list(query);
        if (CollectionUtil.isEmpty(list) || list.size() != actualDeleteFileContext.getFileIdList().size()) {
            throw new MPanBusinessException("文件列表中存在非法文件");
        }
        actualDeleteFileContext.setNestRecords(list);
    }


    /**
     * 文件还原后的操作
     * 1. 发布文件还原事件
     *
     * @param restoreFileContext
     */
    private void AfterRestore(RestoreFileContext restoreFileContext) {
        RestoreFileEvent restoreFileEvent = new RestoreFileEvent(restoreFileContext.getFileIdList());
        streamProducer.sendMessage(PanChannels.FILE_RESTORE_OUTPUT, restoreFileEvent);

    }

    /**
     * 执行还原的动作
     *
     * @param restoreFileContext
     */
    private void doRestore(RestoreFileContext restoreFileContext) {
        List<MPanUserFile> userFileList = restoreFileContext.getUserFileList();
        userFileList.forEach(record -> {
            record.setDelFlag(FileConstants.NO);
            record.setUpdateUser(restoreFileContext.getUserId());
            record.setUpdateTime(new Date());
        });
        boolean res = iUserFileService.updateBatchById(userFileList);
        if (!res) {
            throw new MPanBusinessException("还原失败");
        }
    }

    /**
     * 不允许还原的两种情况
     * 1. 要还原的文件列表中存在 同一目录下同名文件
     * 2. 要还原的文件的名字已经在该文件的父目录中存在
     *
     * @param restoreFileContext
     */
    private void checkRestoreFilename(RestoreFileContext restoreFileContext) {
        List<MPanUserFile> userFileList = restoreFileContext.getUserFileList();
        Set<String> fileNameSet = userFileList.stream().map(record -> record.getFilename() + "_" + record.getParentId()).collect(Collectors.toSet());
        if (fileNameSet.size() != userFileList.size()) {
            throw new MPanBusinessException("还原失败：还原的文件列表中存在同一父目录下同名文件");
        }
        userFileList.forEach(record -> {
            LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
            query.eq(MPanUserFile::getUserId, record.getUserId());
            query.eq(MPanUserFile::getFilename, record.getFilename());
            query.eq(MPanUserFile::getParentId, record.getParentId());
            query.eq(MPanUserFile::getDelFlag, FileConstants.NO);
            if (iUserFileService.count(query) > 0) {
                throw new MPanBusinessException("还原失败：还原的文件名在该文件的父目录中已存在");
            }
        });
    }

    /**
     * 检查文件和用户的合法性
     *
     * @param restoreFileContext
     */
    private void checkRestorePermission(RestoreFileContext restoreFileContext) {
        List<MPanUserFile> records = iUserFileService.listByIds(restoreFileContext.getFileIdList());
        if (!Objects.equals(records.size(), restoreFileContext.getFileIdList().size())) {
            throw new MPanBusinessException("文件列表中存在非法文件");
        }
        Set<Long> userIdSet = records.stream().map(MPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1 || !userIdSet.contains(restoreFileContext.getUserId())) {
            throw new MPanBusinessException("当前用户无权操作该文件");
        }
        restoreFileContext.setUserFileList(records);
    }

}
