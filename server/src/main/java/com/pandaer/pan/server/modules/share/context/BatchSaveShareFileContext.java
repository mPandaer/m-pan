package com.pandaer.pan.server.modules.share.context;

import lombok.Data;

import java.util.List;

/**
 * 批量保存分享记录与分享文件关联关系上下文
 */
@Data
public class BatchSaveShareFileContext {

    private Long shareId;

    private List<Long> fileIdList;

    private Long userId;
}
