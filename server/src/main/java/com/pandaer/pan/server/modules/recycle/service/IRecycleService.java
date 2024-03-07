package com.pandaer.pan.server.modules.recycle.service;

import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.ActualDeleteFileContext;
import com.pandaer.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;

import java.util.List;

public interface IRecycleService {

    /**
     * 获取回收站列表
     * @param queryRecycleFileListContext 查询回收站列表上下文
     * @return 回收站列表
     */
    List<UserFileVO> queryRecycleFileList(QueryRecycleFileListContext queryRecycleFileListContext);


    /**
     * 批量还原文件
     * @param restoreFileContext
     */
    void restore(RestoreFileContext restoreFileContext);


    /**
     * 批量彻底删除文件
     * @param actualDeleteFileContext
     */
    void actualDelete(ActualDeleteFileContext actualDeleteFileContext);
}
