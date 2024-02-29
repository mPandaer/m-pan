package com.pandaer.pan.storage.engine.core;

import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;

/**
 * 文件存储的顶级接口
 */
public interface StorageEngine {
    void storeFile(StoreFileContext storeFileContext) throws IOException;


    void deleteFile(DeleteFileContext deleteFileContext) throws IOException;


    void storeChunk(StoreFileChunkContext context) throws IOException;
}
