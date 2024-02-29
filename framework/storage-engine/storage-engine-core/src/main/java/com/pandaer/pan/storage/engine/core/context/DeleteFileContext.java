package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.util.List;

@Data
public class DeleteFileContext {

    /**
     * 批量删除的物理文件路径
     */
    private List<String> realFilePathList;
}
