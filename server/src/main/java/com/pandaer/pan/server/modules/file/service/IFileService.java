package com.pandaer.pan.server.modules.file.service;

import com.pandaer.pan.server.modules.file.context.SaveFileContext;
import com.pandaer.pan.server.modules.file.domain.MPanFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;

import java.util.Date;

/**
* @author pandaer
* @description 针对表【m_pan_file(物理文件信息表)】的数据库操作Service
* @createDate 2024-02-25 18:36:40
*/
public interface IFileService extends IService<MPanFile> {

    MPanFile getFileWithIdentifier(String identifier);

    MPanFile saveRealFileRecord(Long fileId, String filename, String realPath, Long totalSize, String fileSizeDesc, String fileSuffix, String filePreviewContentType, String identifier, Long userId, Date date);

    void saveFile(SaveFileContext saveFileContext);
}
