package com.pandaer.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.mapper.MPanUserFileMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author pandaer
 * @description 针对表【m_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-02-25 18:36:40
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<MPanUserFileMapper, MPanUserFile>
        implements IUserFileService {

    @Override
    public Long creatFolder(CreateFolderContext context) {
        return saveUserFile(context.getUserId(), context.getParentId(), null,
                context.getFolderName(), FileConstants.YES, null, null);
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
}




