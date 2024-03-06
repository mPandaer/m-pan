package com.pandaer.pan.storage.engine.fastdfs;

import cn.hutool.core.collection.CollectionUtil;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.storage.engine.core.AbstractStorageEngine;
import com.pandaer.pan.storage.engine.core.context.*;
import com.pandaer.pan.storage.engine.fastdfs.config.FastDFSStorageEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class FastDfsStorageEngine extends AbstractStorageEngine {

    @Autowired
    private FastDFSStorageEngineConfig config;


    @Autowired
    private FastFileStorageClient client;


    @Override
    protected void doStoreFile(StoreFileContext storeFileContext) throws IOException {
        StorePath storePath = client.uploadFile(config.getGroup(), storeFileContext.getInputStream(),
                storeFileContext.getTotalSize(), FileUtil.getFileExtName(storeFileContext.getFilename()));
        storeFileContext.setRealPath(storePath.getFullPath());
    }

    @Override
    protected void doDeleteFile(DeleteFileContext deleteFileContext) throws IOException {
        List<String> realFilePathList = deleteFileContext.getRealFilePathList();
        if (!CollectionUtil.isEmpty(realFilePathList)) {
            realFilePathList.forEach(realFilePath -> client.deleteFile(realFilePath));
        }
    }

    @Override
    protected void doStoreChunkFile(StoreFileChunkContext context) throws IOException {
        throw new MPanBusinessException("fastdfs不支持分片上传");
    }

    @Override
    protected void doMergeChunk(MergeChunkContext context) throws IOException {
        throw new MPanBusinessException("fastdfs不支持分片上传");
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        String realFilePath = context.getRealFilePath();
        String group = realFilePath.substring(MPanConstants.ZERO_INT, realFilePath.indexOf(MPanConstants.SLASH_STR));
        String path = realFilePath.substring(realFilePath.indexOf(MPanConstants.SLASH_STR) + MPanConstants.ONE_INT);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = client.downloadFile(group, path, downloadByteArray);
        OutputStream outputStream = context.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();


    }
}
