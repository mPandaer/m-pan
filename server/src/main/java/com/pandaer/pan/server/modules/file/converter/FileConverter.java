package com.pandaer.pan.server.modules.file.converter;

import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.po.*;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileConverter {
    
    
    @Mapping(source = "parentId",target = "parentId")
    @Mapping(source = "fileTypeList",target = "fileTypeList")
    @Mapping(source = "userId",target = "userId")
    @Mapping(source = "delFlag",target = "delFlag")
    QueryFileListContext genContextInQueryFileList(Long parentId, List<Integer> fileTypeList, Long userId, Integer delFlag);
    
    
    UserFileVO entity2VOInQueryFileList(MPanUserFile mPanUserFile);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "parentId",expression = "java(com.pandaer.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    CreateFolderContext PO2ContextInCreateFolder(CreateFolderPO createFolderPO);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "parentId",expression = "java(com.pandaer.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getParentId()))")
    @Mapping(target = "fileId",expression = "java(com.pandaer.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getFileId()))")
    UpdateFilenameContext PO2ContextInUpdateFilename(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "fileIdList",expression = "java(deleteFileWithRecyclePO.getFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()))")
    DeleteFileWithRecycleContext PO2ContextInDeleteFileWithRecycle(DeleteFileWithRecyclePO deleteFileWithRecyclePO);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "parentId",expression = "java(com.pandaer.pan.core.utils.IdUtil.decrypt(secFileUploadPO.getParentId()))")
    SecFileUploadContext PO2ContextInSecFileUpload(SecFileUploadPO secFileUploadPO);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "parentId",expression = "java(com.pandaer.pan.core.utils.IdUtil.decrypt(singleFileUploadPO.getParentId()))")
    SingleFileUploadContext PO2ContextInSingleFileUpload(SingleFileUploadPO singleFileUploadPO);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "realFileEntity",ignore = true)
    SaveFileContext context2contextInSaveFile(SingleFileUploadContext context);

    @Mapping(target = "realPath",ignore = true)
    StoreFileContext context2contextInStoreFileData(SaveFileContext saveFileContext);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    ChunkDataUploadContext PO2ContextInChunkDataUpload(ChunkDataUploadPO chunkDataUploadPO);

    SaveFileChunkContext context2contextInSaveChunkFile(ChunkDataUploadContext context);

    @Mapping(target = "realPath",ignore = true)
    StoreFileChunkContext context2contextInSaveFileChunk(SaveFileChunkContext saveFileChunkContext);

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    QueryUploadedFileChunkContext PO2ContextInQueryUploadedFileChunk(QueryUploadedFileChunkPO queryUploadedFileChunkPO);
}
