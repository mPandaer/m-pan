package com.pandaer.pan.storage.engine.local;

import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.storage.engine.core.AbstractStorageEngine;
import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;
import com.pandaer.pan.storage.engine.local.config.LocalStorageEngineConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfigProperties properties;

    /**
     * 保存文件数据
     * @param storeFileContext
     * @throws IOException
     */
    @Override
    protected void doStoreFile(StoreFileContext storeFileContext) throws IOException {
        String basePath = properties.getBasePath();
        String realFilePath = FileUtil.genRealFilePath(basePath,storeFileContext.getFilename());
        FileUtil.writeStream2File(storeFileContext.getInputStream(),new File(realFilePath),storeFileContext.getTotalSize());
        storeFileContext.setRealPath(realFilePath);
    }

    /**
     * 批量删除物理文件
     * @param deleteFileContext
     * @throws IOException
     */
    @Override
    protected void doDeleteFile(DeleteFileContext deleteFileContext) throws IOException {
        FileUtil.deleteFile(deleteFileContext.getRealFilePathList());
    }

    @Override
    protected void doStoreChunkFile(StoreFileChunkContext context) throws IOException {
        String basePath = properties.getChunkBasePath();
        String realFilePath = FileUtil.genRealChunkFilePath(basePath,context.getIdentifier(),context.getCurrentChunkNumber());
        FileUtil.writeStream2File(context.getInputStream(),new File(realFilePath),context.getCurrentChunkSize());
        context.setRealPath(realFilePath);
    }

}
