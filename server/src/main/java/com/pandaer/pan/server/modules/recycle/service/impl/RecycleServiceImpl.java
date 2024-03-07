package com.pandaer.pan.server.modules.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.server.common.event.file.RestoreFileEvent;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;
import com.pandaer.pan.server.modules.recycle.service.IRecycleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecycleServiceImpl implements IRecycleService, ApplicationContextAware {

    @Autowired
    private IUserFileService iUserFileService;

    private ApplicationContext applicationContext;


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
     * 文件还原后的操作
     * 1. 发布文件还原事件
     * @param restoreFileContext
     */
    private void AfterRestore(RestoreFileContext restoreFileContext) {
        RestoreFileEvent restoreFileEvent = new RestoreFileEvent(this, restoreFileContext.getFileIdList());
        applicationContext.publishEvent(restoreFileEvent);

    }

    /**
     * 执行还原的动作
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}