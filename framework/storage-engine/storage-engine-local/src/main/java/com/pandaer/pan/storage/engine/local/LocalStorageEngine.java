package com.pandaer.pan.storage.engine.local;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.storage.engine.core.AbstractStorageEngine;
import com.pandaer.pan.storage.engine.core.context.*;
import com.pandaer.pan.storage.engine.local.config.LocalStorageEngineConfigProperties;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

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

    /**
     * 合并分片文件
     * @param context
     */
    @Override
    protected void doMergeChunk(MergeChunkContext context) throws IOException {
        String realFilePath = FileUtil.genRealFilePath(properties.getBasePath(),context.getFilename());
        FileUtil.createRealFile(new File(realFilePath));
        for (String chunkPath : context.getChunkPathList()) {
            FileUtil.appendWrite(Paths.get(realFilePath),new File(chunkPath).toPath());
        }
        FileUtil.deleteFile(context.getChunkPathList());
        context.setRealFilePath(realFilePath);
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        File file = new File(context.getRealFilePath());
        if(!file.exists()){
            throw new MPanBusinessException("文件不存在");
        }
        FileUtil.writeFile2OutputStream(new FileInputStream(file),context.getOutputStream(),file.length());

    }


}
