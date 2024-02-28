package com.pandaer.pan.server.modules.file.service;

import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;

import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-02-25 18:36:40
*/
public interface IUserFileService extends IService<MPanUserFile> {

    Long creatFolder(CreateFolderContext context);

    MPanUserFile getRootUserFileByUserId(Long userId);

    List<UserFileVO> getFileList(QueryFileListContext context);

    void updateFilename(UpdateFilenameContext context);

    void deleteFileWithRecycle(DeleteFileWithRecycleContext context);
}
