package com.pandaer.pan.server.modules.file;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pandaer.pan.server.modules.user.UserTest.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FileTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    //查询文件列表成功
    @Test
    public void testQueryFileListSuccess() {
        Long userId = userRegister();
        QueryFileListContext context = new QueryFileListContext();
        context.setFileTypeList(null);
        context.setParentId(FileConstants.ROOT_FOLDER_PARENT_ID);
        context.setDelFlag(FileConstants.NO);
        context.setUserId(userId);
        List<UserFileVO> list = userFileService.getFileList(context);
        Assert.assertTrue(list != null && list.size() == 1);
    }

    //创建文件夹成功
    @Test
    public void testCreateFolderSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);
    }

    //文件重命名成功
    @Test
    public void testUpdateFilenameSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("旧旧的文件夹");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setParentId(currentUser.getRootFileId());
        updateFilenameContext.setUserId(userId);
        userFileService.updateFilename(updateFilenameContext);

    }

    //文件重命名失败 -- 文件不存在
    @Test(expected = MPanBusinessException.class)
    public void testUpdateFilenameFailFileNotExit() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("旧旧的文件夹");
        updateFilenameContext.setFileId(fileId + 100);
        updateFilenameContext.setParentId(currentUser.getRootFileId());
        updateFilenameContext.setUserId(userId);
        userFileService.updateFilename(updateFilenameContext);
    }

    //文件重命名失败 -- 新文件名已经存在
    @Test(expected = MPanBusinessException.class)
    public void testUpdateFilenameNewFilenameExit() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //再次创建
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("旧旧的文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        userFileService.creatFolder(createFolderContext);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("旧旧的文件夹");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setParentId(currentUser.getRootFileId());
        updateFilenameContext.setUserId(userId);
        userFileService.updateFilename(updateFilenameContext);
    }


    //删除文件到回收站成功
    @Test
    public void testDeleteFileWithRecycleSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);
    }

    //删除文件失败 -- 文件ID无效
    @Test(expected = MPanBusinessException.class)
    public void testDeleteFileWithRecycleFailFileIdError() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId + 1));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);
    }

    //删除文件失败 -- 用户没有操作权限
    @Test(expected = MPanBusinessException.class)
    public void testDeleteFileWithRecycleFailUserNotPrivilege() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId + 1);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);
    }



    private Long userRegister() {
        UserRegisterContext context = getUserRegisterContext();
        return userService.register(context);
    }

    private CurrentUserVO current(Long userId) {
        return userService.getCurrentUser(userId);
    }

    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }


}
