package com.pandaer.pan.server.modules.file.converter;

import com.pandaer.pan.server.modules.file.context.BreadcrumbContext;
import com.pandaer.pan.server.modules.file.context.ChunkDataUploadContext;
import com.pandaer.pan.server.modules.file.context.CopyFileContext;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.MergeChunkFileContext;
import com.pandaer.pan.server.modules.file.context.MoveFileContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.QueryUploadedFileChunkContext;
import com.pandaer.pan.server.modules.file.context.SaveFileChunkContext;
import com.pandaer.pan.server.modules.file.context.SaveFileContext;
import com.pandaer.pan.server.modules.file.context.SearchFileContext;
import com.pandaer.pan.server.modules.file.context.SecFileUploadContext;
import com.pandaer.pan.server.modules.file.context.SingleFileUploadContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.po.ChunkDataUploadPO;
import com.pandaer.pan.server.modules.file.po.CopyFilePO;
import com.pandaer.pan.server.modules.file.po.CreateFolderPO;
import com.pandaer.pan.server.modules.file.po.DeleteFileWithRecyclePO;
import com.pandaer.pan.server.modules.file.po.MergeChunkFilePO;
import com.pandaer.pan.server.modules.file.po.MoveFilePO;
import com.pandaer.pan.server.modules.file.po.QueryUploadedFileChunkPO;
import com.pandaer.pan.server.modules.file.po.SearchFilePO;
import com.pandaer.pan.server.modules.file.po.SecFileUploadPO;
import com.pandaer.pan.server.modules.file.po.SingleFileUploadPO;
import com.pandaer.pan.server.modules.file.po.UpdateFilenamePO;
import com.pandaer.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.pandaer.pan.server.modules.file.vo.SearchFileInfoVO;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;
import com.pandaer.pan.server.modules.recycle.po.RestoreFilePO;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-07T11:28:00+0800",
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

    @Override
    public SecFileUploadContext PO2ContextInSecFileUpload(SecFileUploadPO secFileUploadPO) {
        if ( secFileUploadPO == null ) {
            return null;
        }

        SecFileUploadContext secFileUploadContext = new SecFileUploadContext();

        secFileUploadContext.setFilename( secFileUploadPO.getFilename() );
        secFileUploadContext.setIdentifier( secFileUploadPO.getIdentifier() );

        secFileUploadContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        secFileUploadContext.setParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(secFileUploadPO.getParentId()) );

        return secFileUploadContext;
    }

    @Override
    public SingleFileUploadContext PO2ContextInSingleFileUpload(SingleFileUploadPO singleFileUploadPO) {
        if ( singleFileUploadPO == null ) {
            return null;
        }

        SingleFileUploadContext singleFileUploadContext = new SingleFileUploadContext();

        singleFileUploadContext.setFilename( singleFileUploadPO.getFilename() );
        singleFileUploadContext.setIdentifier( singleFileUploadPO.getIdentifier() );
        singleFileUploadContext.setTotalSize( singleFileUploadPO.getTotalSize() );
        singleFileUploadContext.setFileData( singleFileUploadPO.getFileData() );

        singleFileUploadContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        singleFileUploadContext.setParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(singleFileUploadPO.getParentId()) );

        return singleFileUploadContext;
    }

    @Override
    public SaveFileContext context2contextInSaveFile(SingleFileUploadContext context) {
        if ( context == null ) {
            return null;
        }

        SaveFileContext saveFileContext = new SaveFileContext();

        saveFileContext.setFilename( context.getFilename() );
        saveFileContext.setIdentifier( context.getIdentifier() );
        saveFileContext.setTotalSize( context.getTotalSize() );
        saveFileContext.setFileData( context.getFileData() );

        saveFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );

        return saveFileContext;
    }

    @Override
    public StoreFileContext context2contextInStoreFileData(SaveFileContext saveFileContext) {
        if ( saveFileContext == null ) {
            return null;
        }

        StoreFileContext storeFileContext = new StoreFileContext();

        storeFileContext.setFilename( saveFileContext.getFilename() );
        storeFileContext.setTotalSize( saveFileContext.getTotalSize() );

        return storeFileContext;
    }

    @Override
    public ChunkDataUploadContext PO2ContextInChunkDataUpload(ChunkDataUploadPO chunkDataUploadPO) {
        if ( chunkDataUploadPO == null ) {
            return null;
        }

        ChunkDataUploadContext chunkDataUploadContext = new ChunkDataUploadContext();

        chunkDataUploadContext.setFilename( chunkDataUploadPO.getFilename() );
        chunkDataUploadContext.setIdentifier( chunkDataUploadPO.getIdentifier() );
        chunkDataUploadContext.setTotalSize( chunkDataUploadPO.getTotalSize() );
        chunkDataUploadContext.setTotalChunks( chunkDataUploadPO.getTotalChunks() );
        chunkDataUploadContext.setCurrentChunkNumber( chunkDataUploadPO.getCurrentChunkNumber() );
        chunkDataUploadContext.setCurrentChunkSize( chunkDataUploadPO.getCurrentChunkSize() );
        chunkDataUploadContext.setFileData( chunkDataUploadPO.getFileData() );

        chunkDataUploadContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );

        return chunkDataUploadContext;
    }

    @Override
    public SaveFileChunkContext context2contextInSaveChunkFile(ChunkDataUploadContext context) {
        if ( context == null ) {
            return null;
        }

        SaveFileChunkContext saveFileChunkContext = new SaveFileChunkContext();

        saveFileChunkContext.setIdentifier( context.getIdentifier() );
        saveFileChunkContext.setCurrentChunkNumber( context.getCurrentChunkNumber() );
        saveFileChunkContext.setCurrentChunkSize( context.getCurrentChunkSize() );
        saveFileChunkContext.setFileData( context.getFileData() );
        saveFileChunkContext.setUserId( context.getUserId() );
        saveFileChunkContext.setTotalChunks( context.getTotalChunks() );
        saveFileChunkContext.setFilename( context.getFilename() );

        return saveFileChunkContext;
    }

    @Override
    public StoreFileChunkContext context2contextInSaveFileChunk(SaveFileChunkContext saveFileChunkContext) {
        if ( saveFileChunkContext == null ) {
            return null;
        }

        StoreFileChunkContext storeFileChunkContext = new StoreFileChunkContext();

        storeFileChunkContext.setIdentifier( saveFileChunkContext.getIdentifier() );
        storeFileChunkContext.setCurrentChunkNumber( saveFileChunkContext.getCurrentChunkNumber() );
        storeFileChunkContext.setCurrentChunkSize( saveFileChunkContext.getCurrentChunkSize() );
        storeFileChunkContext.setTotalChunks( saveFileChunkContext.getTotalChunks() );
        storeFileChunkContext.setUserId( saveFileChunkContext.getUserId() );
        storeFileChunkContext.setFilename( saveFileChunkContext.getFilename() );

        return storeFileChunkContext;
    }

    @Override
    public QueryUploadedFileChunkContext PO2ContextInQueryUploadedFileChunk(QueryUploadedFileChunkPO queryUploadedFileChunkPO) {
        if ( queryUploadedFileChunkPO == null ) {
            return null;
        }

        QueryUploadedFileChunkContext queryUploadedFileChunkContext = new QueryUploadedFileChunkContext();

        queryUploadedFileChunkContext.setIdentifier( queryUploadedFileChunkPO.getIdentifier() );

        queryUploadedFileChunkContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );

        return queryUploadedFileChunkContext;
    }

    @Override
    public MergeChunkFileContext PO2ContextInMergeChunkFile(MergeChunkFilePO mergeChunkFilePO) {
        if ( mergeChunkFilePO == null ) {
            return null;
        }

        MergeChunkFileContext mergeChunkFileContext = new MergeChunkFileContext();

        mergeChunkFileContext.setFilename( mergeChunkFilePO.getFilename() );
        mergeChunkFileContext.setIdentifier( mergeChunkFilePO.getIdentifier() );
        mergeChunkFileContext.setTotalSize( mergeChunkFilePO.getTotalSize() );
        mergeChunkFileContext.setTotalChunks( mergeChunkFilePO.getTotalChunks() );

        mergeChunkFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        mergeChunkFileContext.setParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(mergeChunkFilePO.getParentId()) );

        return mergeChunkFileContext;
    }

    @Override
    public FolderTreeNodeVO entity2VOInFolderTree(MPanUserFile entity) {
        if ( entity == null ) {
            return null;
        }

        FolderTreeNodeVO folderTreeNodeVO = new FolderTreeNodeVO();

        folderTreeNodeVO.setId( entity.getFileId() );
        folderTreeNodeVO.setLabel( entity.getFilename() );
        folderTreeNodeVO.setParentId( entity.getParentId() );

        folderTreeNodeVO.setChildren( com.google.common.collect.Lists.newArrayList() );

        return folderTreeNodeVO;
    }

    @Override
    public MoveFileContext PO2ContextInMoveFile(MoveFilePO moveFilePO) {
        if ( moveFilePO == null ) {
            return null;
        }

        MoveFileContext moveFileContext = new MoveFileContext();

        moveFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        moveFileContext.setFileIdList( moveFilePO.getFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );
        moveFileContext.setTargetParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(moveFilePO.getTargetParentId()) );

        return moveFileContext;
    }

    @Override
    public CopyFileContext PO2ContextInCopyFile(CopyFilePO copyFilePO) {
        if ( copyFilePO == null ) {
            return null;
        }

        CopyFileContext copyFileContext = new CopyFileContext();

        copyFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        copyFileContext.setCopyFileIdList( copyFilePO.getCopyFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );
        copyFileContext.setTargetParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(copyFilePO.getTargetParentId()) );

        return copyFileContext;
    }

    @Override
    public SearchFileContext params2Context(String keyword) {
        if ( keyword == null ) {
            return null;
        }

        SearchFileContext searchFileContext = new SearchFileContext();

        searchFileContext.setKeyword( keyword );

        searchFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );

        return searchFileContext;
    }

    @Override
    public SearchFileContext PO2ContextInSearchFile(SearchFilePO searchFilePO) {
        if ( searchFilePO == null ) {
            return null;
        }

        SearchFileContext searchFileContext = new SearchFileContext();

        searchFileContext.setKeyword( searchFilePO.getKeyword() );

        searchFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );

        return searchFileContext;
    }

    @Override
    public SearchFileInfoVO entity2VOInSearchFile(MPanUserFile mPanUserFile) {
        if ( mPanUserFile == null ) {
            return null;
        }

        SearchFileInfoVO searchFileInfoVO = new SearchFileInfoVO();

        searchFileInfoVO.setFileId( mPanUserFile.getFileId() );
        searchFileInfoVO.setParentId( mPanUserFile.getParentId() );
        searchFileInfoVO.setFolderFlag( mPanUserFile.getFolderFlag() );
        searchFileInfoVO.setFileSizeDesc( mPanUserFile.getFileSizeDesc() );
        searchFileInfoVO.setFileType( mPanUserFile.getFileType() );
        searchFileInfoVO.setUpdateTime( mPanUserFile.getUpdateTime() );

        return searchFileInfoVO;
    }

    @Override
    public BreadcrumbContext params2ContextInGetBreadcrumb(String fileId) {
        if ( fileId == null ) {
            return null;
        }

        BreadcrumbContext breadcrumbContext = new BreadcrumbContext();

        breadcrumbContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        breadcrumbContext.setFileId( com.pandaer.pan.core.utils.IdUtil.decrypt(fileId) );

        return breadcrumbContext;
    }

    @Override
    public RestoreFileContext PO2ContextInRestoreFile(RestoreFilePO restoreFilePO) {
        if ( restoreFilePO == null ) {
            return null;
        }

        RestoreFileContext restoreFileContext = new RestoreFileContext();

        restoreFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        restoreFileContext.setFileIdList( restoreFilePO.getFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );

        return restoreFileContext;
    }
}
