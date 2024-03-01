package com.pandaer.pan.server.modules.file.service;

import com.pandaer.pan.server.modules.file.context.MergeChunkFileContext;
import com.pandaer.pan.server.modules.file.context.SaveFileChunkContext;
import com.pandaer.pan.server.modules.file.domain.MPanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_file_chunk(文件分片信息表)】的数据库操作Service
* @createDate 2024-02-25 18:36:40
*/
public interface IFileChunkService extends IService<MPanFileChunk> {

    void saveFileChunk(SaveFileChunkContext saveFileChunkContext);


    List<MPanFileChunk> getFileChunkListWithIdentifierAndUserId(String identifier, Long userId);

    void mergeChunkFile(MergeChunkFileContext context);
}
