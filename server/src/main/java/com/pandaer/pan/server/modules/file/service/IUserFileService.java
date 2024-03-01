package com.pandaer.pan.server.modules.file.service;

import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.file.vo.ChunkDataUploadVO;
import com.pandaer.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.pandaer.pan.server.modules.file.vo.UploadedFileChunkVO;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;

import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-02-25 18:36:40
*/
public interface IUserFileService extends IService<MPanUserFile> {

    Long creatFolder(CreateFolderContext context);

    MPanUserFile getRootUserFileByUserId(Long userId);

    List<UserFileVO> getFileList(QueryFileListContext context);

    void updateFilename(UpdateFilenameContext context);

    void deleteFileWithRecycle(DeleteFileWithRecycleContext context);

    boolean secFileUpload(SecFileUploadContext context);

    void singleFileUpload(SingleFileUploadContext context);

    ChunkDataUploadVO chunkDataUpload(ChunkDataUploadContext context);

    UploadedFileChunkVO queryUploadedFileChunk(QueryUploadedFileChunkContext context);

    void mergeChunkFile(MergeChunkFileContext context);

    void download(FileDownloadContext fileDownloadContext);

    void preview(FilePreviewContext filePreviewContext);

    List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext filePreviewContext);
}
