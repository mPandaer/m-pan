package com.pandaer.pan.storage.engine.core;

import com.pandaer.pan.storage.engine.core.context.*;

import java.io.IOException;

/**
 * 文件存储的顶级接口
 */
public interface StorageEngine {
    void storeFile(StoreFileContext storeFileContext) throws IOException;


    void deleteFile(DeleteFileContext deleteFileContext) throws IOException;


    void storeChunk(StoreFileChunkContext context) throws IOException;

    void mergeChunk(MergeChunkContext context) throws IOException;

    void readFile(ReadFileContext context) throws IOException;
}
