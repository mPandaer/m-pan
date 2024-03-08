package com.pandaer.pan.server.modules.share.service;

import com.pandaer.pan.server.modules.share.context.BatchSaveShareFileContext;
import com.pandaer.pan.server.modules.share.domain.MPanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author pandaer
* @description 针对表【m_pan_share_file(用户分享文件表)】的数据库操作Service
* @createDate 2024-02-25 18:38:16
*/
public interface IShareFileService extends IService<MPanShareFile> {

    /**
     * 批量保存分享记录与分享文件关联关系
     * @param batchSaveShareFileContext
     */
    void batchSaveShareFile(BatchSaveShareFileContext batchSaveShareFileContext);
}
