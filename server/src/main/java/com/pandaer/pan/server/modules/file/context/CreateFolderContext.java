package com.pandaer.pan.server.modules.file.context;

import lombok.Data;

@Data
public class CreateFolderContext {
    /**
     * 父文件夹Id
     */
    private Long parentId;
    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;
}
