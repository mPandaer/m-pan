package com.pandaer.pan.server.modules.file.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.event.file.DeleteFileWithRecycleEvent;
import com.pandaer.pan.server.common.utils.HttpUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanFile;
import com.pandaer.pan.server.modules.file.domain.MPanFileChunk;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.enums.FileType;
import com.pandaer.pan.server.modules.file.service.IFileChunkService;
import com.pandaer.pan.server.modules.file.service.IFileService;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.mapper.MPanUserFileMapper;
import com.pandaer.pan.server.modules.file.vo.ChunkDataUploadVO;
import com.pandaer.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.pandaer.pan.server.modules.file.vo.UploadedFileChunkVO;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.user.constants.UserConstants;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.storage.engine.core.StorageEngine;
import com.pandaer.pan.storage.engine.core.context.ReadFileContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;


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


    /**
     * 文件秒传的功能实现
     * 1.检查文件是否存在
     * 2.存在则秒传成功挂载关联关系，没有则秒传失败
     * @param context
     */
    @Override
    public boolean secFileUpload(SecFileUploadContext context) {
        getRealFileWithIdentifier(context);
        MPanFile realFileEntity = context.getRealFileEntity();
        if (realFileEntity == null) {
            return false;
        }
        saveUserFileRecord(context);
        return true;
    }

    /**
     * 单文件上传的业务实现
     * 1. 文件数据的保存
     * 2. 增加文件记录
     * 3. 增加文件与用户之间的关系记录
     * @param context
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void singleFileUpload(SingleFileUploadContext context) {
        saveFileDataAndRecord(context);
        MPanFile realFileEntity = context.getRealFileEntity();
        saveUserFile(context.getUserId(),context.getParentId(),
                realFileEntity.getFileId(),context.getFilename(),FileConstants.NO,realFileEntity.getFileSizeDesc(),
                FileType.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())));
    }

    /**
     * 文件分片数据上传
     * 1. 保存文件分片数据
     * 2. 增加文件分片记录
     * 3. 校验是全部分片已经上传
     * @param context
     * @return
     */
    @Override
    public ChunkDataUploadVO chunkDataUpload(ChunkDataUploadContext context) {
        SaveFileChunkContext saveFileChunkContext = fileConverter.context2contextInSaveChunkFile(context);
        fileChunkService.saveFileChunk(saveFileChunkContext);
        ChunkDataUploadVO vo = new ChunkDataUploadVO();
        vo.setChunkNumber(saveFileChunkContext.getCurrentChunkNumber());
        vo.setMerge(saveFileChunkContext.getMerge());
        return vo;
    }

    @Override
    public UploadedFileChunkVO queryUploadedFileChunk(QueryUploadedFileChunkContext context) {
        List<MPanFileChunk> fileChunkList = fileChunkService.getFileChunkListWithIdentifierAndUserId(context.getIdentifier(),context.getUserId());
        List<Integer> chunkNumberList = fileChunkList.stream().map(MPanFileChunk::getChunkNumber).collect(Collectors.toList());
        UploadedFileChunkVO uploadedFileChunkVO = new UploadedFileChunkVO();
        uploadedFileChunkVO.setUploadedChunkNumberList(chunkNumberList);
        return uploadedFileChunkVO;
    }

    /**
     * 合并文件分片业务实现
     * 1. 委托给FileChunkService实现
     * 2. 校验分片数据是否完整
     * 3. 合并分片数据并增加文件记录
     * 4. 发出文件合并事件 (由监听文件合并事件的监听器，删除分片数据以及分片记录) （也可以不立即删除，过期后，由定时器统一删除）
     * 5. 建立当前文件记录与用户记录之间的映射关系
     * @param context
     */
    @Override
    public void mergeChunkFile(MergeChunkFileContext context) {
        fileChunkService.mergeChunkFile(context);
        //建立映射关系
        saveUserFile(context.getUserId(), context.getParentId(),context.getRealFileId(),context.getFilename(),FileConstants.NO,
                FileUtil.byteCount2DisplaySize(context.getTotalSize()),
                FileType.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())));
    }

    /**
     * 文件下载
     * 1. 参数校验，判断文件是否存在
     * 2. 将文件写入响应体中
     * @param fileDownloadContext
     */
    @Override
    public void download(FileDownloadContext fileDownloadContext) {
        MPanUserFile record = getById(fileDownloadContext.getFileId());
        checkOperationPermission(record,fileDownloadContext.getUserId());
        if (isFolder(record)) {
            throw new MPanBusinessException("文件夹暂不支持下载");
        }
        doDownloadFile(record,fileDownloadContext.getResponse());
    }

    /**
     * 文件预览
     * 1. 参数校验，判断文件是否存在
     * 2. 将文件写入响应体中
     * 3. 执行预览操作
     * @param filePreviewContext
     */
    @Override
    public void preview(FilePreviewContext filePreviewContext) {
        MPanUserFile record = getById(filePreviewContext.getFileId());
        checkOperationPermission(record,filePreviewContext.getUserId());
        if (isFolder(record)) {
            throw new MPanBusinessException("文件夹暂不支持下载");
        }
        doPreviewFile(record,filePreviewContext.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * 1. 查询用户的文件夹列表
     * 2. 在内存中构建文件夹树
     * @param filePreviewContext
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext filePreviewContext) {
        List<MPanUserFile> folderList = queryFolderListByUserId(filePreviewContext.getUserId());
        List<FolderTreeNodeVO> folderTreeNodeVOList = aassembleFolderTreeNodeVOList(folderList);
        return folderTreeNodeVOList;
    }

    /**
     * 构建文件夹树
     * @param folderList
     * @return
     */
    private List<FolderTreeNodeVO> aassembleFolderTreeNodeVOList(List<MPanUserFile> folderList) {
        if(CollectionUtil.isEmpty(folderList)) {
            return Lists.newArrayList();
        }

        List<FolderTreeNodeVO> mappedFolderList = folderList.stream().map(fileConverter::entity2VOInFolderTree).collect(Collectors.toList());
        Map<Long, List<FolderTreeNodeVO>> nodeMap = mappedFolderList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));
        for (FolderTreeNodeVO node : mappedFolderList) {
            List<FolderTreeNodeVO> children = nodeMap.get(node.getId());
            if (CollectionUtil.isNotEmpty(children)) {
                node.setChildren(children);
            }
        }

        return mappedFolderList.stream().filter(node -> Objects.equals(node.getParentId(), FileConstants.ROOT_FOLDER_PARENT_ID)).collect(Collectors.toList());
    }

    private List<MPanUserFile> queryFolderListByUserId(Long userId) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId,userId)
                .eq(MPanUserFile::getFolderFlag,FileConstants.YES)
                .eq(MPanUserFile::getDelFlag,FileConstants.NO);
        return list(query);
    }

    private void doPreviewFile(MPanUserFile record, HttpServletResponse response) {
        MPanFile fileEntity = fileService.getById(record.getFileId());
        if (Objects.isNull(fileEntity)) {
            throw new MPanBusinessException("文件不存在");
        }
        addCorsResponseHeader(response,fileEntity.getFilePreviewContentType());
        realFile2OutputStream(fileEntity.getRealPath(),response);
    }

    /**
     * 执行下载操作
     * 1. 获取到文件的存储路径
     * 2. 添加跨域响应头
     * 3. 拼装下载文件的名称 长度等等信息
     * 4. 委托文件存储引擎将文件数据写入到响应的输出流中
     * @param record
     * @param response
     */
    private void doDownloadFile(MPanUserFile record, HttpServletResponse response) {
        MPanFile fileEntity = fileService.getById(record.getFileId());
        if (Objects.isNull(fileEntity)) {
            throw new MPanBusinessException("文件不存在");
        }
        addCorsResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response,record,fileEntity);
        realFile2OutputStream(fileEntity.getRealPath(),response);
    }


    /**
     * 委托文件存储引擎将文件数据写入到响应的输出流中
     * @param realPath
     * @param response
     */
    private void realFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext readFileContext = new ReadFileContext();
            readFileContext.setOutputStream(response.getOutputStream());
            readFileContext.setRealFilePath(realPath);
            storageEngine.readFile(readFileContext);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MPanBusinessException("文件下载失败");
        }

    }

    private void addDownloadAttribute(HttpServletResponse response, MPanUserFile record, MPanFile fileEntity) {
        try {
            String filename = record.getFilename();
            response.addHeader(FileConstants.CONTENT_DISPOSITION_STR,FileConstants.ATTACHMENT_PREFIX_STR + filename);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MPanBusinessException("文件下载失败");
        }

        response.setContentLengthLong(Long.parseLong(fileEntity.getFileSize()));
    }

    private void addCorsResponseHeader(HttpServletResponse response, String contentType) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        //todo 这两行代码很疑惑
        response.addHeader(FileConstants.CONTENT_TYPE_STR,contentType);
        response.setContentType(contentType);

    }

    private boolean isFolder(MPanUserFile record) {
        if (Objects.isNull(record)) {
            throw new MPanBusinessException("文件不存在");
        }
        return Objects.equals(record.getFolderFlag(), FileConstants.YES);
    }

    private void checkOperationPermission(MPanUserFile record,Long userId) {
        if (Objects.isNull(record)) {
            throw new MPanBusinessException("文件不存在");
        }
        if (!Objects.equals(record.getUserId(),userId)) {
            throw new MPanBusinessException("当前登录用户没有操作权限");
        }
    }

    private void saveFileDataAndRecord(SingleFileUploadContext context) {
        SaveFileContext saveFileContext = fileConverter.context2contextInSaveFile(context);
        fileService.saveFile(saveFileContext);
        context.setRealFileEntity(saveFileContext.getRealFileEntity());
    }

    private void saveUserFileRecord(SecFileUploadContext context) {
        MPanFile realFile = new MPanFile();
        this.saveUserFile(context.getUserId(),context.getParentId(),realFile.getFileId(),context.getFilename(),
                FileConstants.NO,realFile.getFileSizeDesc(),
                FileType.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())));
    }

    private void getRealFileWithIdentifier(SecFileUploadContext context) {
        MPanFile realFile = fileService.getFileWithIdentifier(context.getIdentifier());
        context.setRealFileEntity(realFile);
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




