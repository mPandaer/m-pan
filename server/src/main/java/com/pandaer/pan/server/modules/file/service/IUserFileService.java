package com.pandaer.pan.server.modules.file.service;

import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author pandaer
* @description 针对表【m_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-02-25 18:36:40
*/
public interface IUserFileService extends IService<MPanUserFile> {

    Long creatFolder(CreateFolderContext context);

    MPanUserFile getRootUserFileByUserId(Long userId);
}
