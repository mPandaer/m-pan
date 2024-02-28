package com.pandaer.pan.server.modules.file.converter;

import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.po.CreateFolderPO;
import com.pandaer.pan.server.modules.file.po.DeleteFileWithRecyclePO;
import com.pandaer.pan.server.modules.file.po.UpdateFilenamePO;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
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
}
