package com.pandaer.pan.server.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.modules.share.context.BatchSaveShareFileContext;
import com.pandaer.pan.server.modules.share.domain.MPanShareFile;
import com.pandaer.pan.server.modules.share.service.IShareFileService;
import com.pandaer.pan.server.modules.share.mapper.MPanShareFileMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_share_file(用户分享文件表)】的数据库操作Service实现
* @createDate 2024-02-25 18:38:16
*/
@Service
public class ShareFileServiceImpl extends ServiceImpl<MPanShareFileMapper, MPanShareFile>
    implements IShareFileService {

    @Override
    public void batchSaveShareFile(BatchSaveShareFileContext context) {
        Long shareId = context.getShareId();
        List<Long> fileIdList = context.getFileIdList();
        Long userId = context.getUserId();

        List<MPanShareFile> list = new ArrayList<>();
        for (Long fileId : fileIdList) {
            MPanShareFile shareFile = new MPanShareFile();
            shareFile.setId(IdUtil.get());
            shareFile.setShareId(shareId);
            shareFile.setFileId(fileId);
            shareFile.setCreateUser(userId);
            shareFile.setCreateTime(new Date());
            list.add(shareFile);
        }

        if (!saveBatch(list)) {
            throw new RuntimeException("批量保存分享记录与分享文件关联关系失败");
        }
    }
}




