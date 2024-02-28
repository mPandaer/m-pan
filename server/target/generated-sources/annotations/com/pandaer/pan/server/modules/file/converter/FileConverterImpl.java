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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-28T12:57:07+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_402 (Oracle Corporation)"
)
@Component
public class FileConverterImpl implements FileConverter {

    @Override
    public QueryFileListContext genContextInQueryFileList(Long parentId, List<Integer> fileTypeList, Long userId, Integer delFlag) {
        if ( parentId == null && fileTypeList == null && userId == null && delFlag == null ) {
            return null;
        }

        QueryFileListContext queryFileListContext = new QueryFileListContext();

        queryFileListContext.setParentId( parentId );
        List<Integer> list = fileTypeList;
        if ( list != null ) {
            queryFileListContext.setFileTypeList( new ArrayList<Integer>( list ) );
        }
        queryFileListContext.setUserId( userId );
        queryFileListContext.setDelFlag( delFlag );

        return queryFileListContext;
    }

    @Override
    public UserFileVO entity2VOInQueryFileList(MPanUserFile mPanUserFile) {
        if ( mPanUserFile == null ) {
            return null;
        }

        UserFileVO userFileVO = new UserFileVO();

        userFileVO.setFileId( mPanUserFile.getFileId() );
        userFileVO.setParentId( mPanUserFile.getParentId() );
        userFileVO.setFilename( mPanUserFile.getFilename() );
        userFileVO.setFolderFlag( mPanUserFile.getFolderFlag() );
        userFileVO.setFileSizeDesc( mPanUserFile.getFileSizeDesc() );
        userFileVO.setFileType( mPanUserFile.getFileType() );
        userFileVO.setUpdateTime( mPanUserFile.getUpdateTime() );

        return userFileVO;
    }

    @Override
    public CreateFolderContext PO2ContextInCreateFolder(CreateFolderPO createFolderPO) {
        if ( createFolderPO == null ) {
            return null;
        }

        CreateFolderContext createFolderContext = new CreateFolderContext();

        createFolderContext.setFolderName( createFolderPO.getFolderName() );

        createFolderContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        createFolderContext.setParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()) );

        return createFolderContext;
    }

    @Override
    public UpdateFilenameContext PO2ContextInUpdateFilename(UpdateFilenamePO updateFilenamePO) {
        if ( updateFilenamePO == null ) {
            return null;
        }

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();

        updateFilenameContext.setNewFilename( updateFilenamePO.getNewFilename() );

        updateFilenameContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        updateFilenameContext.setParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getParentId()) );
        updateFilenameContext.setFileId( com.pandaer.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getFileId()) );

        return updateFilenameContext;
    }

    @Override
    public DeleteFileWithRecycleContext PO2ContextInDeleteFileWithRecycle(DeleteFileWithRecyclePO deleteFileWithRecyclePO) {
        if ( deleteFileWithRecyclePO == null ) {
            return null;
        }

        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();

        deleteFileWithRecycleContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        deleteFileWithRecycleContext.setFileIdList( deleteFileWithRecyclePO.getFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );

        return deleteFileWithRecycleContext;
    }
}
