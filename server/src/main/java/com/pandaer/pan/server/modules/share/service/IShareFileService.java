package com.pandaer.pan.server.modules.share.service;

import com.pandaer.pan.server.modules.share.context.BatchSaveShareFileContext;
import com.pandaer.pan.server.modules.share.domain.MPanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

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

    /**
     * 通过文件列表集合获取到关联的分享记录ID
     * @param allFileIdList
     * @return
     */
    Set<Long> getShareIdListByFileId(List<Long> allFileIdList);

    /**
     * 根据shareId获取对应的文件Id
     * @param id
     * @return
     */
    List<Long> getFileIdListInCurShare(Long id);
}
