package com.pandaer.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.event.file.DeleteFileWithRecycleEvent;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.mapper.MPanUserFileMapper;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import org.apache.commons.lang3.StringUtils;
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

/**
 * @author pandaer
 * @description 针对表【m_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-02-25 18:36:40
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<MPanUserFileMapper, MPanUserFile>
        implements IUserFileService, ApplicationContextAware {


    @Autowired
    private FileConverter fileConverter;

    private ApplicationContext applicationContext;

    @Override
    public Long creatFolder(CreateFolderContext context) {
        return saveUserFile(context.getUserId(), context.getParentId(), null,
                context.getFolderName(), FileConstants.YES, null, null);
    }

    @Override
    public MPanUserFile getRootUserFileByUserId(Long userId) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId,userId)
                .eq(MPanUserFile::getFolderFlag,FileConstants.YES)
                .eq(MPanUserFile::getDelFlag,FileConstants.NO)
                .eq(MPanUserFile::getParentId, FileConstants.ROOT_FOLDER_PARENT_ID);
        return getOne(query);
    }

    @Override
    public List<UserFileVO> getFileList(QueryFileListContext context) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getParentId,context.getParentId())
                .eq(MPanUserFile::getUserId,context.getUserId())
                .eq(MPanUserFile::getDelFlag,context.getDelFlag());
        if (context.getFileTypeList() != null) {
            query.in(MPanUserFile::getFileType,context.getFileTypeList());
        }
        List<MPanUserFile> list = list(query);
        return list.stream().map(fileConverter::entity2VOInQueryFileList).collect(Collectors.toList());
    }


    /**
     * 判断文件是否存在
     * 判断新文件名是否合法
     * 更新文件名
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUserFileAndNewFilename(context);
        doUpdateFileName(context);

    }

    /**
     * 检查文件是否存在
     * 检查当前用户是否具有删除权限
     * 删除文件到回收站
     * 发布文件删除事件
     * @param context
     */
    @Override
    public void deleteFileWithRecycle(DeleteFileWithRecycleContext context) {
        checkUserFile(context);
        batchDeleteFileWithRecycle(context);
        publishDeleteFileEvent(context);
    }

    private void publishDeleteFileEvent(DeleteFileWithRecycleContext context) {
        DeleteFileWithRecycleEvent deleteFileWithRecycleEvent = new DeleteFileWithRecycleEvent(this, context.getFileIdList());
        applicationContext.publishEvent(deleteFileWithRecycleEvent);
    }

    private void batchDeleteFileWithRecycle(DeleteFileWithRecycleContext context) {
        LambdaUpdateWrapper<MPanUserFile> update = new LambdaUpdateWrapper<>();
        update.in(MPanUserFile::getFileId,context.getFileIdList())
                .eq(MPanUserFile::getUserId,context.getUserId());
        update.set(MPanUserFile::getDelFlag,FileConstants.YES)
                .set(MPanUserFile::getUpdateTime,new Date())
                .set(MPanUserFile::getUpdateUser,context.getUserId());
        if (!this.update(update)) {
            throw new MPanBusinessException("移动文件到回收站失败");
        }
    }

    private void checkUserFile(DeleteFileWithRecycleContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<MPanUserFile> userFileEntityList = listByIds(fileIdList);
        Set<Long> dbFileIdSet = userFileEntityList.stream().map(MPanUserFile::getFileId).collect(Collectors.toSet());
        if (!Objects.equals(dbFileIdSet.size(),fileIdList.size())) {
            throw new MPanBusinessException("存在不合法的文件ID");
        }
        Set<Long> userIdSet = userFileEntityList.stream().map(MPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1 || !userIdSet.contains(context.getUserId())) {
            throw new MPanBusinessException("存在没有操作权限的文件");
        }
    }

    private void doUpdateFileName(UpdateFilenameContext context) {
        String newFilename = context.getNewFilename();
        MPanUserFile entity = context.getEntity();
        entity.setFilename(newFilename);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(context.getUserId());
        if (!updateById(entity)) {
            throw new MPanBusinessException("文件重命名失败");
        }
    }

    /**
     * 文件是否存在
     * 登录用户是否有修改权限
     * 新旧文件名是否一致
     * 新文件名是否已经存在
     * @param context
     */
    private void checkUserFileAndNewFilename(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        MPanUserFile entity = getById(fileId);
        if (entity == null) {
            throw new MPanBusinessException("文件不存在");
        }
        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new MPanBusinessException("当前用户没有修改权限");
        }
        if(StringUtils.equals(entity.getFilename(),context.getNewFilename())) {
            throw new MPanBusinessException("文件名没有变化");
        }

        String newFilename = context.getNewFilename();
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getFilename,newFilename)
                .eq(MPanUserFile::getFolderFlag,entity.getFolderFlag())
                .eq(MPanUserFile::getUserId,context.getUserId())
                .eq(MPanUserFile::getParentId,context.getParentId());
        int count = count(query);
        if (count > 0) {
            throw new MPanBusinessException("新文件名在当前文件夹下已经存在");
        }
        context.setEntity(entity);
    }

    /**
     * 保存用户与文件之间的关系记录
     *
     * @param userId
     * @param parentId
     * @param realFileId
     * @param filename
     * @param isFolder
     * @param fileSizeDesc
     * @param fileType
     * @return
     */
    private Long saveUserFile(
            Long userId, Long parentId, Long realFileId,
            String filename, Integer isFolder, String fileSizeDesc,
            Integer fileType) {
        MPanUserFile entity = assembleUserFile(userId, parentId, realFileId, filename, isFolder, fileSizeDesc, fileType);
        if (!save(entity)) {
            throw new MPanBusinessException("保存用户文件记录失败");
        }
        return entity.getFileId();
    }

    /**
     * 构建并填充记录
     * 检查是否存在重名记录
     *
     * @param userId
     * @param parentId
     * @param realFileId
     * @param filename
     * @param isFolder
     * @param fileSizeDesc
     * @param fileType
     * @return
     */
    private MPanUserFile assembleUserFile(Long userId, Long parentId, Long realFileId, String filename, Integer isFolder, String fileSizeDesc, Integer fileType) {
        MPanUserFile userFile = new MPanUserFile();
        userFile.setFileId(IdUtil.get());
        userFile.setUserId(userId);
        userFile.setParentId(parentId);
        userFile.setRealFileId(realFileId);
        userFile.setFilename(filename);
        userFile.setFolderFlag(isFolder);
        userFile.setFileSizeDesc(fileSizeDesc);
        userFile.setFileType(fileType);
        userFile.setDelFlag(FileConstants.NO);
        userFile.setCreateUser(userId);
        userFile.setCreateTime(new Date());
        userFile.setUpdateUser(userId);
        userFile.setUpdateTime(new Date());
        handleRepeatFileName(userFile);
        return userFile;
    }

    /**
     * 检查并处理重复文件名的问题
     * @param userFile
     */
    private void handleRepeatFileName(MPanUserFile userFile) {
        String filename = userFile.getFilename();
        int lastIndex = filename.lastIndexOf(MPanConstants.POINT_STR);
        String filenameNoSuffix = filename;
        String fileSuffix = "";
        if (lastIndex != -1) {
            filenameNoSuffix = filename.substring(0, lastIndex);
            fileSuffix = filename.replace(filenameNoSuffix, "");
        }

        Integer count = getCountWithFileName(userFile, filenameNoSuffix);
        if (count == 0) {
            return;
        }
        userFile.setFilename(genNewFilename(filenameNoSuffix, count, fileSuffix));
    }

    /**
     * 生成新的文件名
     * @param filenameNoSuffix
     * @param count
     * @param fileSuffix
     * @return
     */
    private String genNewFilename(String filenameNoSuffix, Integer count, String fileSuffix) {
        return filenameNoSuffix + FileConstants.LEFT_PAIR + count + FileConstants.RIGHT_PAIR + fileSuffix;
    }

    /**
     * 获取同文件名的数量
     * @param userFile
     * @param filenameNoSuffix
     * @return
     */
    private Integer getCountWithFileName(MPanUserFile userFile, String filenameNoSuffix) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId, userFile.getFileId())
                .eq(MPanUserFile::getParentId, userFile.getParentId())
                .eq(MPanUserFile::getDelFlag, FileConstants.NO)
                .eq(MPanUserFile::getFolderFlag, userFile.getFolderFlag())
                .likeLeft(MPanUserFile::getFilename, filenameNoSuffix);
        return count(query);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}




