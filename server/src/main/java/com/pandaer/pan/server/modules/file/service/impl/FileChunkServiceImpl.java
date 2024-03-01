package com.pandaer.pan.server.modules.file.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.config.PanServerConfigProperties;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.MergeChunkFileContext;
import com.pandaer.pan.server.modules.file.context.SaveFileChunkContext;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanFileChunk;
import com.pandaer.pan.server.modules.file.service.IFileChunkService;
import com.pandaer.pan.server.modules.file.mapper.MPanFileChunkMapper;
import com.pandaer.pan.server.modules.file.service.IFileService;
import com.pandaer.pan.storage.engine.core.StorageEngine;
import com.pandaer.pan.storage.engine.core.context.MergeChunkContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author pandaer
* @description 针对表【m_pan_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2024-02-25 18:36:40
*/
@Service
public class FileChunkServiceImpl extends ServiceImpl<MPanFileChunkMapper, MPanFileChunk>
    implements IFileChunkService {


    @Autowired
    private PanServerConfigProperties properties;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileChunkService fileChunkService;


    @Autowired
    private StorageEngine storageEngine;
    /**
     * todo synchronized 加在这里范围太大
     * 保存分片文件以及记录
     * 检查文件是否分片上传完毕
     * @param saveFileChunkContext
     */
    @Override
    public synchronized void saveFileChunk(SaveFileChunkContext saveFileChunkContext) {
        doSaveFileChunk(saveFileChunkContext);
        checkMerge(saveFileChunkContext); //检查文件是否可以合并
    }

    @Override
    public List<MPanFileChunk> getFileChunkListWithIdentifierAndUserId(String identifier, Long userId) {
        LambdaQueryWrapper<MPanFileChunk> query = new LambdaQueryWrapper<>();
        query.eq(MPanFileChunk::getIdentifier,identifier)
                .eq(MPanFileChunk::getCreateUser,userId)
                .gt(MPanFileChunk::getExpirationTime,new Date());
        return list(query);
    }

    /**
     * 合并文件分片业务实现
     * 1. 校验分片数据是否完整
     * 2. 合并分片数据并增加文件记录
     * 3. 发出文件合并事件 (由监听文件合并事件的监听器，删除分片数据以及分片记录) （也可以不立即删除，过期后，由定时器统一删除）
     * 目前的选择方案是 在合并分片数据后就直接删除
     * @param context
     */
    @Override
    public void mergeChunkFile(MergeChunkFileContext context) {
        validChunkFileCompleteness(context);
        doMergeChunkFile(context);

    }

    /**
     * 1.合并分片数据
     * 2.增加文件记录
     * 3.删除分片记录
     * @param context
     */
    @Transactional
    public void doMergeChunkFile(MergeChunkFileContext context) {
        mergeChunkData(context);
        Long realFileId = IdUtil.get();
        fileService.saveRealFileRecord(realFileId, context.getFilename(), context.getRealFilePath(),
                context.getTotalSize(), FileUtil.byteCount2DisplaySize(context.getTotalSize()),FileUtil.getFileSuffix(context.getFilename()),
                FileUtil.getFilePreviewContentType(FileUtil.getFileSuffix(context.getFilename())), context.getIdentifier(), context.getUserId(),
                new Date());
        context.setRealFileId(realFileId);
        //删除分片记录
        List<MPanFileChunk> chunkList = context.getChunkList();
        fileChunkService.removeByIds(chunkList.stream().map(MPanFileChunk::getId).collect(Collectors.toList()));
    }

    private void mergeChunkData(MergeChunkFileContext context) {
        MergeChunkContext mergeChunkContext = new MergeChunkContext();
        mergeChunkContext.setFilename(context.getFilename());
        mergeChunkContext.setTotalSize(context.getTotalSize());
        mergeChunkContext.setIdentifier(context.getIdentifier());
        List<String> pathList = context.getChunkList().stream()
                .sorted(Comparator.comparing(MPanFileChunk::getChunkNumber))
                .map(MPanFileChunk::getRealPath).collect(Collectors.toList());
        mergeChunkContext.setChunkPathList(pathList);
        try {
            storageEngine.mergeChunk(mergeChunkContext);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MPanFrameworkException("合并分片文件失败");
        }
        context.setRealFilePath(mergeChunkContext.getRealFilePath());
    }

    /**
     * 校验分片数据的完备性
     * @param context
     */
    private void validChunkFileCompleteness(MergeChunkFileContext context) {
        String identifier = context.getIdentifier();
        Long userId = context.getUserId();
        LambdaQueryWrapper<MPanFileChunk> query = new LambdaQueryWrapper<>();
        query.eq(MPanFileChunk::getIdentifier,identifier)
                .eq(MPanFileChunk::getCreateUser,userId)
                .ge(MPanFileChunk::getExpirationTime,new Date());
        List<MPanFileChunk> chunkList = list(query);
        if (chunkList.size() != context.getTotalChunks()) {
            throw new MPanBusinessException("分片文件不完整");
        }
        context.setChunkList(chunkList);
    }


    //查询已经上传的文件分片数，和总数比较是否可以合并文件
    private void checkMerge(SaveFileChunkContext saveFileChunkContext) {
        LambdaQueryWrapper<MPanFileChunk> query = new LambdaQueryWrapper<>();
        query.eq(MPanFileChunk::getIdentifier,saveFileChunkContext.getIdentifier())
                .eq(MPanFileChunk::getCreateUser,saveFileChunkContext.getUserId());
        int count = count(query);
        if (count != saveFileChunkContext.getTotalChunks()) {
            saveFileChunkContext.setMerge(FileConstants.NO);
            return;
        }
        saveFileChunkContext.setMerge(FileConstants.YES);
    }

    private void doSaveFileChunk(SaveFileChunkContext saveFileChunkContext) {
        storeFileChunk(saveFileChunkContext);
        saveFileRecord(saveFileChunkContext);
    }

    private void saveFileRecord(SaveFileChunkContext saveFileChunkContext) {
        MPanFileChunk fileChunk = new MPanFileChunk();
        fileChunk.setId(IdUtil.get());
        fileChunk.setIdentifier(saveFileChunkContext.getIdentifier());
        fileChunk.setRealPath(saveFileChunkContext.getRealPath());
        fileChunk.setChunkNumber(saveFileChunkContext.getCurrentChunkNumber());
        fileChunk.setCreateUser(saveFileChunkContext.getUserId());
        fileChunk.setCreateTime(new Date());
        fileChunk.setExpirationTime(DateUtil.offsetDay(new Date(),properties.getChunkFileExpirationDays()));
        if (!save(fileChunk)) {
            throw new MPanBusinessException("保存分片文件记录失败");
        }
    }

    /**
     * 委托文件存储引擎 保存数据
     * @param saveFileChunkContext
     */
    private void storeFileChunk(SaveFileChunkContext saveFileChunkContext) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConverter.context2contextInSaveFileChunk(saveFileChunkContext);
            storeFileChunkContext.setInputStream(saveFileChunkContext.getFileData().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            saveFileChunkContext.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MPanBusinessException("保存文件分片失败");
        }
    }
}




