package com.pandaer.pan.storage.engine.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.pandaer.pan.cache.core.constants.CacheConstants;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 顶级文件存储引擎的顶级父类
 */


public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    public Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new MPanFrameworkException("存储引擎中获取缓存失败");
        }
        return cacheManager.getCache(CacheConstants.M_PAN_CACHE_NAME);
    }


    /**
     * 1.校验参数
     * 2.保存文件
     * @param storeFileContext
     * @throws IOException
     */
    @Override
    public void storeFile(StoreFileContext storeFileContext) throws IOException {
        validStoreFileInfo(storeFileContext);
        doStoreFile(storeFileContext);
    }

    protected abstract void doStoreFile(StoreFileContext storeFileContext) throws IOException;

    private void validStoreFileInfo(StoreFileContext storeFileContext) {
        String filename = storeFileContext.getFilename();
        if (StringUtils.isBlank(filename)) {
            throw new MPanBusinessException("文件名不能为空");
        }
        Long totalSize = storeFileContext.getTotalSize();
        if (Objects.isNull(totalSize) || totalSize <= 0) {
            throw new MPanBusinessException("文件大小不合法");
        }
        InputStream inputStream = storeFileContext.getInputStream();
        if (Objects.isNull(inputStream)) {
            throw new MPanBusinessException("文件数据不能为空");
        }
    }

    /**
     * 1.校验参数
     * 2. 删除文件
     * @param deleteFileContext
     * @throws IOException
     */
    @Override
    public void deleteFile(DeleteFileContext deleteFileContext) throws IOException {
        validDeleteFileInfo(deleteFileContext);
        doDeleteFile(deleteFileContext);
    }

    protected abstract void doDeleteFile(DeleteFileContext deleteFileContext) throws IOException;

    private void validDeleteFileInfo(DeleteFileContext deleteFileContext) {
        List<String> realFilePathList = deleteFileContext.getRealFilePathList();
        if (CollUtil.isEmpty(realFilePathList)) {
            throw new MPanBusinessException("没有要删除的物理文件路径");
        }
    }


    /**
     * 1.参数校验
     * 2. 保存分片文件
     * @param context
     * @throws IOException
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        checkChunkFileInfo(context);
        doStoreChunkFile(context);
    }

    protected abstract void doStoreChunkFile(StoreFileChunkContext context) throws IOException;

    private void checkChunkFileInfo(StoreFileChunkContext context) {
        Assert.notBlank(context.getIdentifier(),"文件唯一标识不能为空");
        Assert.notNull(context.getCurrentChunkNumber(),"文件当前分片数不能为空");
        Assert.notNull(context.getCurrentChunkSize(),"文件当前分片大小不能为空");
        Assert.notNull(context.getInputStream(),"文件数据不能为空");

    }

}
