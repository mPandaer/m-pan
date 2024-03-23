package com.pandaer.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.stream.channel.PanChannels;
import com.pandaer.pan.server.common.stream.event.log.ErrorLogEvent;
import com.pandaer.pan.server.modules.file.context.SaveFileContext;
import com.pandaer.pan.storage.engine.core.context.DeleteFileContext;
import com.pandaer.pan.storage.engine.core.context.StoreFileContext;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanFile;
import com.pandaer.pan.server.modules.file.service.IFileService;
import com.pandaer.pan.server.modules.file.mapper.MPanFileMapper;
import com.pandaer.pan.storage.engine.core.StorageEngine;
import com.pandaer.pan.stream.core.IStreamProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
* @author pandaer
* @description 针对表【m_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-02-25 18:36:40
*/
@Service
public class FileServiceImpl extends ServiceImpl<MPanFileMapper, MPanFile>
    implements IFileService {

    @Autowired
    private StorageEngine storageEngine;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    @Qualifier("defaultStreamProducer")
    private IStreamProducer streamProducer;

    @Override
    public MPanFile getFileWithIdentifier(String identifier) {
        LambdaQueryWrapper<MPanFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanFile::getIdentifier,identifier);
        return getOne(query);
    }

    @Override
    public MPanFile saveRealFileRecord(Long fileId, String filename, String realPath, Long totalSize, String fileSizeDesc, String fileSuffix, String filePreviewContentType, String identifier, Long userId, Date date) {
        MPanFile entity = new MPanFile();
        entity.setFileId(fileId);
        entity.setFilename(filename);
        entity.setRealPath(realPath);
        entity.setFileSize(String.valueOf(totalSize));
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileSuffix(fileSuffix);
        entity.setFilePreviewContentType(filePreviewContentType);
        entity.setIdentifier(identifier);
        entity.setCreateUser(userId);
        entity.setCreateTime(date);
        if (!save(entity)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Collections.singletonList(realPath));
                storageEngine.deleteFile(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                ErrorLogEvent event = new ErrorLogEvent("删除文件失败，文件路径 " + realPath,userId);
                streamProducer.sendMessage(PanChannels.ERROR_LOG_INPUT,event);
                throw new MPanBusinessException("保存文件记录失败");
            }
        }
        return entity;
    }


    /**
     * 保存文件业务实现
     * 1. 保存文件数据
     * 2. 增加文件记录
     * @param saveFileContext
     */
    @Override
    public void saveFile(SaveFileContext saveFileContext) {
        storeFileData(saveFileContext);
        MPanFile realFile = this.saveRealFileRecord(
                IdUtil.get(),saveFileContext.getFilename(),saveFileContext.getRealPath(),
                saveFileContext.getTotalSize(),FileUtil.byteCount2DisplaySize(saveFileContext.getTotalSize()), FileUtil.getFileSuffix(saveFileContext.getFilename()),
                FileUtil.getFilePreviewContentType(FileUtil.getFileSuffix(saveFileContext.getFilename())),saveFileContext.getIdentifier(),saveFileContext.getUserId(),new Date()
        );
        saveFileContext.setRealFileEntity(realFile);

    }

    /**
     * 保存文件数据
     * @param saveFileContext
     */
    private void storeFileData(SaveFileContext saveFileContext) {
        try {
            StoreFileContext storeFileContext = fileConverter.context2contextInStoreFileData(saveFileContext);
            storeFileContext.setInputStream(saveFileContext.getFileData().getInputStream());
            storageEngine.storeFile(storeFileContext);
            saveFileContext.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            throw new MPanBusinessException("文件保存失败");
        }
    }

}




