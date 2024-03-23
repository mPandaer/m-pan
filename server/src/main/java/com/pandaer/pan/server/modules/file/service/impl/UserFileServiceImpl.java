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
import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.file.DeleteFileWithRecycleEvent;
import com.pandaer.pan.server.common.stream.event.file.SearchFileEvent;
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
import com.pandaer.pan.server.modules.file.vo.*;
import com.pandaer.pan.server.modules.user.constants.UserConstants;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.storage.engine.core.StorageEngine;
import com.pandaer.pan.storage.engine.core.context.ReadFileContext;
import com.pandaer.pan.stream.core.IStreamProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        implements IUserFileService {


    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;

    @Autowired
    @Qualifier("defaultStreamProducer")
    private IStreamProducer streamProducer;

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
        if (context.getParentId() != null) {
            query.eq(MPanUserFile::getParentId,context.getParentId());
        }
        if (context.getUserId() != null) {
            query.eq(MPanUserFile::getUserId,context.getUserId());
        }
        if (context.getDelFlag() != null) {
            query.eq(MPanUserFile::getDelFlag,context.getDelFlag());
        }
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
        vo.setChunkNumber(saveFileChunkContext.getChunkNumber());
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

    @Override
    public void shareDownload(FileDownloadContext fileDownloadContext) {
        MPanUserFile record = getById(fileDownloadContext.getFileId());
        if (Objects.isNull(record)) {
            throw new MPanBusinessException("文件不存在");
        }
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
     * 批量移动文件
     * 1.检查文件是否存在
     * 2.检查目标文件夹是否存在
     * 3.检查移动的文件列表中是否包含目标文件夹及其子文件夹 避免出现循环引用
     * 3.检查用户是否有操作权限
     * 4.修改文件与用户之间的关系记录
     * @param moveFileContext
     */
    @Override
    @Transactional
    public void moveFile(MoveFileContext moveFileContext) {
        checkMoveFileCondition(moveFileContext);
        doMoveFile(moveFileContext);
    }

    /**
     * 批量复制文件
     * 1. 检验文件的合法性以及目标文件夹的合法性
     * 2. 判断用户是否有操作权限
     * 3. 复制文件
     * @param copyFileContext
     */
    @Override
    @Transactional
    public void copyFile(CopyFileContext copyFileContext) {
        checkCopyFileCondition(copyFileContext);
        doCopyFile(copyFileContext);
    }

    /**
     * 文件搜索
     * 1. 根据关键字获取文件信息
     * 2. 将文件信息实体转换为VO
     * 3. 执行文件搜索的后续操作
     * @param searchFileContext
     * @return
     */
    @Override
    public List<SearchFileInfoVO> searchFile(SearchFileContext searchFileContext) {
        List<SearchFileInfoVO> voList = doSearchFile(searchFileContext);
        afterSearchFile(searchFileContext);
        return voList;
    }


    /**
     * 校验文件是否存在
     * 获取全部文件夹信息
     * 拼接面包屑导航列表
     * @param breadcrumbContext
     * @return
     */
    @Override
    public List<BreadcrumbVO> getBreadcrumb(BreadcrumbContext breadcrumbContext) {
        MPanUserFile userFileEntity = checkUserFileExist(breadcrumbContext.getFileId(),breadcrumbContext.getUserId());
        if (!Objects.equals(userFileEntity.getFolderFlag(),FileConstants.YES)) {
            throw new MPanBusinessException("查询的fileId必须是一个文件夹");
        }

        List<MPanUserFile> folderList = getAllFolderList(breadcrumbContext.getUserId());
        Map<Long, MPanUserFile> id2FileMap = folderList.stream().collect(Collectors.toMap(MPanUserFile::getFileId, entity -> entity));
        LinkedList<BreadcrumbVO> breadcrumbList = new LinkedList<>();

        MPanUserFile curFolder = userFileEntity;
        while (curFolder!= null && curFolder.getFileId() != 0) {
            BreadcrumbVO breadcrumbVO = new BreadcrumbVO();
            breadcrumbVO.setFileId(curFolder.getFileId());
            breadcrumbVO.setFilename(curFolder.getFilename());
            breadcrumbList.addFirst(breadcrumbVO);
            curFolder = id2FileMap.get(curFolder.getParentId());
        }
        return breadcrumbList;
    }


    /**
     * 将嵌套的文件列表转换为不嵌套的文件列表
     * 输入：要删除的文件以及文件夹
     * 输出：将文件夹内的所有文件添加的列表中
     * @param nestRecords
     * @return
     */
    @Override
    public List<MPanUserFile> findAllRecords(List<MPanUserFile> nestRecords) {
        List<MPanUserFile> allRecords = new ArrayList<>();
        for (MPanUserFile record : nestRecords) {
            allRecords.add(record);
            if (Objects.equals(record.getFolderFlag(),FileConstants.YES)) {
                LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
                query.eq(MPanUserFile::getParentId,record.getFileId())
                        .eq(MPanUserFile::getUserId,record.getUserId());
                List<MPanUserFile> childrenList = list(query);
                if (CollectionUtil.isNotEmpty(childrenList)) {
                    List<MPanUserFile> list = findAllRecords(childrenList);
                    allRecords.addAll(list);
                }

            }
        }
        return allRecords;
    }

    /**
     * 根据用户ID获取文件夹列表
     * @param userId
     * @return
     */
    private List<MPanUserFile> getAllFolderList(Long userId) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId,userId)
                .eq(MPanUserFile::getFolderFlag,FileConstants.YES)
                .eq(MPanUserFile::getDelFlag,FileConstants.NO);
        List<MPanUserFile> list = list(query);
        if (list.isEmpty()) {
            throw new MPanBusinessException("文件夹不存在");
        }
        return list;
    }

    /**
     * 根据文件ID以及用户ID判断文件是否存在
     * @param fileId
     * @param userId
     * @return
     */
    private MPanUserFile checkUserFileExist(Long fileId, Long userId) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getFileId,fileId)
                .eq(MPanUserFile::getUserId,userId)
                .eq(MPanUserFile::getDelFlag,FileConstants.NO);
        MPanUserFile userFileEntity = getOne(query);
        if (Objects.isNull(userFileEntity)) {
            throw new MPanBusinessException("文件不存在");
        }
        return userFileEntity;
    }

    /**
     * 发布文件搜索事件
     * 1. 主要作用是为保存用户搜索历史提供一个触发点
     * @param searchFileContext
     */
    private void afterSearchFile(SearchFileContext searchFileContext) {
        SearchFileEvent searchFileEvent = new SearchFileEvent(searchFileContext.getKeyword(), searchFileContext.getUserId());
        streamProducer.sendMessage(PanChannels.USER_SEARCH_OUTPUT,searchFileEvent);
    }

    private List<SearchFileInfoVO> doSearchFile(SearchFileContext searchFileContext) {
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId,searchFileContext.getUserId())
                .eq(MPanUserFile::getDelFlag,FileConstants.NO)
                .likeRight(MPanUserFile::getFilename,searchFileContext.getKeyword());
        List<MPanUserFile> fileList = list(query);
        List<SearchFileInfoVO> list = fileList.stream().map(fileConverter::entity2VOInSearchFile).collect(Collectors.toList());
        if (list.isEmpty()) {
            return list;
        }
        List<Long> parentIdList = list.stream().map(SearchFileInfoVO::getParentId).collect(Collectors.toList());
        List<MPanUserFile> parentFolderList = listByIds(parentIdList);
        Map<Long, String> id2nameMap = parentFolderList.stream().collect(Collectors.toMap(MPanUserFile::getFileId, MPanUserFile::getFilename));
        list.forEach(vo -> vo.setParentName(id2nameMap.get(vo.getParentId())));
        return list;
    }

    /**
     * 复制文件，需要考虑文件夹的复制
     * @param copyFileContext
     */
    @Transactional
    public void doCopyFile(CopyFileContext copyFileContext) {
        List<MPanUserFile> needCopyFileList = copyFileContext.getNeedCopyFileList();
        //获取该用户的全部文件夹
        List<MPanUserFile> allFolderList = queryFolderListByUserId(copyFileContext.getUserId()).stream()
                .filter(file -> Objects.equals(file.getFolderFlag(), FileConstants.YES)).collect(Collectors.toList());

        //建立一层文件夹ID与其子文件夹列表的映射关系
        Map<Long, List<MPanUserFile>> allFolderMap = allFolderList.stream()
                .collect(Collectors.groupingBy(MPanUserFile::getParentId));

        List<MPanUserFile> needCopyFolderListNotNested = new ArrayList<>();
        transformCopyFileList(allFolderMap,needCopyFolderListNotNested,needCopyFileList,copyFileContext.getTargetParentId(),copyFileContext.getUserId());
        if (!saveBatch(needCopyFolderListNotNested)) {
            throw new MPanBusinessException("复制文件失败");
        }
    }

    /**
     * 将由嵌套层级的复制文件列表转换为不嵌套的文件夹列表和文件列表
     * @param allFolderMap
     * @param needCopyFolderListNotNested
     * @param needCopyFileList
     */
    private void transformCopyFileList(Map<Long, List<MPanUserFile>> allFolderMap,
                                       List<MPanUserFile> needCopyFolderListNotNested,
                                       List<MPanUserFile> needCopyFileList, Long targetParentId, Long userId) {
        for (MPanUserFile userFile : needCopyFileList) {
            Long newFileId = IdUtil.get();
            Long oldFileId = userFile.getFileId();
            userFile.setFileId(newFileId);
            userFile.setParentId(targetParentId);
            userFile.setCreateUser(userId);
            userFile.setCreateTime(new Date());
            userFile.setUpdateUser(userId);
            userFile.setUpdateTime(new Date());
            handleRepeatFileName(userFile);
            needCopyFolderListNotNested.add(userFile);
            if (Objects.equals(userFile.getFolderFlag(),FileConstants.YES)) {
                List<MPanUserFile> childrenFileList = allFolderMap.get(oldFileId);
                if (CollectionUtil.isNotEmpty(childrenFileList)) {
                    transformCopyFileList(allFolderMap,needCopyFolderListNotNested,childrenFileList,newFileId,userId);
                }
            }
        }
    }

    private void checkCopyFileCondition(CopyFileContext copyFileContext) {
        List<Long> fileIdList = copyFileContext.getCopyFileIdList();
        List<MPanUserFile> needCopyFileList = listByIds(fileIdList);
        if (!Objects.equals(needCopyFileList.size(),fileIdList.size())) {
            throw new MPanBusinessException("存在不合法的文件ID");
        }
        MPanUserFile targetFolder = getById(copyFileContext.getTargetParentId());
        if (Objects.isNull(targetFolder) || Objects.equals(targetFolder.getFolderFlag(),FileConstants.NO)) {
            throw new MPanBusinessException("目标文件夹不存在");
        }
        //需要移动的文件夹列表
        List<MPanUserFile> needMoveFolderList = needCopyFileList.stream()
                .filter(file -> Objects.equals(file.getFolderFlag(), FileConstants.YES)).collect(Collectors.toList());

        //获取该用户的全部文件夹
        List<MPanUserFile> allFolderList = queryFolderListByUserId(copyFileContext.getUserId()).stream()
                .filter(file -> Objects.equals(file.getFolderFlag(), FileConstants.YES)).collect(Collectors.toList());

        //建立一层文件夹ID与其子文件夹列表的映射关系
        Map<Long, List<MPanUserFile>> allFolderMap = allFolderList.stream()
                .collect(Collectors.groupingBy(MPanUserFile::getParentId));

        //非法文件夹列表
        List<MPanUserFile> unLegalFolderList = new ArrayList<>();
        getUnLegalFolderList(allFolderMap,unLegalFolderList,needMoveFolderList);
        List<Long> unLegalFolderIdList = unLegalFolderList.stream().map(MPanUserFile::getFileId).collect(Collectors.toList());
        if (unLegalFolderIdList.contains(copyFileContext.getTargetParentId())) {
            throw new MPanBusinessException("目标文件夹不能是移动文件夹及其子文件夹");
        }
        copyFileContext.setNeedCopyFileList(needCopyFileList);
    }

    /**
     * 执行文件移动操作
     * @param moveFileContext
     */
    @Transactional
    public void doMoveFile(MoveFileContext moveFileContext) {
        List<MPanUserFile> needMoveFileList = moveFileContext.getNeedMoveFileList();
        for (MPanUserFile file : needMoveFileList) {
            file.setParentId(moveFileContext.getTargetParentId());
            file.setUpdateTime(new Date());
            file.setUpdateUser(moveFileContext.getUserId());
            handleRepeatFileName(file); //处理移动的文件到目标文件后重名
        }
        if (!updateBatchById(needMoveFileList)) {
            throw new MPanBusinessException("移动文件失败");
        }
    }

    /**
     * 1.检查文件是否存在
     * 2.检查目标文件夹是否存在
     * 3.检查移动的文件列表中是否包含目标文件夹及其子文件夹 避免出现循环引用
     * 4.检查用户是否有操作权限
     * @param moveFileContext
     */
    private void checkMoveFileCondition(MoveFileContext moveFileContext) {
        List<Long> fileIdList = moveFileContext.getFileIdList();
        List<MPanUserFile> needMoveFileList = listByIds(fileIdList);
        if (!Objects.equals(needMoveFileList.size(),fileIdList.size())) {
            throw new MPanBusinessException("存在不合法的文件ID");
        }
        MPanUserFile targetFolder = getById(moveFileContext.getTargetParentId());
        if (Objects.isNull(targetFolder) || Objects.equals(targetFolder.getFolderFlag(),FileConstants.NO)) {
            throw new MPanBusinessException("目标文件夹不存在");
        }
        //需要移动的文件夹列表
        List<MPanUserFile> needMoveFolderList = needMoveFileList.stream()
                .filter(file -> Objects.equals(file.getFolderFlag(), FileConstants.YES)).collect(Collectors.toList());

        //获取该用户的全部文件夹
        List<MPanUserFile> allFolderList = queryFolderListByUserId(moveFileContext.getUserId()).stream()
                .filter(file -> Objects.equals(file.getFolderFlag(), FileConstants.YES)).collect(Collectors.toList());

        //建立一层文件夹ID与其子文件夹列表的映射关系
        Map<Long, List<MPanUserFile>> allFolderMap = allFolderList.stream()
                .collect(Collectors.groupingBy(MPanUserFile::getParentId));

        //非法文件夹列表
        List<MPanUserFile> unLegalFolderList = new ArrayList<>();
        getUnLegalFolderList(allFolderMap,unLegalFolderList,needMoveFolderList);
        List<Long> unLegalFolderIdList = unLegalFolderList.stream().map(MPanUserFile::getFileId).collect(Collectors.toList());
        if (unLegalFolderIdList.contains(moveFileContext.getTargetParentId())) {
            throw new MPanBusinessException("目标文件夹不能是移动文件夹及其子文件夹");
        }
        moveFileContext.setNeedMoveFileList(needMoveFileList);

    }

    /**
     * 获取非法的文件夹列表
     * @param allFolderMap
     * @param unLegalFolderList
     * @param needMoveFolderList
     */
    private void getUnLegalFolderList(Map<Long, List<MPanUserFile>> allFolderMap, List<MPanUserFile> unLegalFolderList, List<MPanUserFile> needMoveFolderList) {
        for (MPanUserFile userFolder : needMoveFolderList) {
            unLegalFolderList.add(userFolder);
            List<MPanUserFile> childrenFolders = allFolderMap.get(userFolder.getFileId());
            if (!CollectionUtil.isEmpty(childrenFolders)) {
                getUnLegalFolderList(allFolderMap,unLegalFolderList,childrenFolders);
            }
        }
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
        MPanFile fileEntity = fileService.getById(record.getRealFileId());
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
        MPanFile fileEntity = fileService.getById(record.getRealFileId());
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
        DeleteFileWithRecycleEvent deleteFileWithRecycleEvent = new DeleteFileWithRecycleEvent(context.getFileIdList());
        streamProducer.sendMessage(PanChannels.DELETE_FILE_OUTPUT,deleteFileWithRecycleEvent);
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
                .eq(MPanUserFile::getUserId,context.getUserId());
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
        query.eq(MPanUserFile::getUserId, userFile.getUserId())
                .eq(MPanUserFile::getParentId, userFile.getParentId())
                .eq(MPanUserFile::getDelFlag, FileConstants.NO)
                .eq(MPanUserFile::getFolderFlag, userFile.getFolderFlag())
                .likeRight(MPanUserFile::getFilename, filenameNoSuffix);
        return count(query);
    }

}




