package com.pandaer.pan.server.modules.recycle;


import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;
import com.pandaer.pan.server.modules.recycle.service.IRecycleService;
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
import static com.pandaer.pan.server.modules.user.UserTest.ANSWER;

/**
 * 回收站模块测试
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RecycleTest {

    @Autowired
    private IRecycleService recycleService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IUserService userService;

    /**
     * 测试获取回收站列表 成功
     */

    @Test
    public void testRecycles() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //删除文件夹
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 1);
    }


    /**
     * 测试还原文件 成功
     */

    @Test
    public void testRestoreFileSuccess() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //删除文件夹
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 1);

        //还原文件
        RestoreFileContext restoreFileContext = new RestoreFileContext();
        restoreFileContext.setUserId(userId);
        restoreFileContext.setFileIdList(Collections.singletonList(fileId));
        recycleService.restore(restoreFileContext);

        //获取回收站列表
        queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.isEmpty());

    }


    /**
     * 测试还原文件 失败 文件不存在
     */

    @Test(expected = MPanBusinessException.class)
    public void testRestoreFileFailWithFileNotExist() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //删除文件夹
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 1);

        //还原文件
        RestoreFileContext restoreFileContext = new RestoreFileContext();
        restoreFileContext.setUserId(userId);
        restoreFileContext.setFileIdList(Collections.singletonList(fileId + 1));
        recycleService.restore(restoreFileContext);

        //获取回收站列表
        queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId + 1);
        voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.isEmpty());

    }


    /**
     * 测试还原文件 失败 用户无权限
     */

    @Test(expected = MPanBusinessException.class)
    public void testRestoreFileWithUserNotPermission() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //删除文件夹
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 1);

        //还原文件
        RestoreFileContext restoreFileContext = new RestoreFileContext();
        restoreFileContext.setUserId(userId + 1);
        restoreFileContext.setFileIdList(Collections.singletonList(fileId));
        recycleService.restore(restoreFileContext);

        //获取回收站列表
        queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.isEmpty());

    }


    /**
     * 测试还原文件 失败 还原文件列表存在同一目录下同名文件
     */

    @Test(expected = MPanBusinessException.class)
    public void testRestoreFileWithFileName01() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹01
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹01");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId1 = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId1 != null && fileId1 > 0);

        //创建文件夹02
        createFolderContext.setFolderName("新建文件夹02");
        Long fileId2 = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId2 != null && fileId2 > 0);

        //删除文件夹01
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId1));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //删除文件夹02
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId2));
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 2);

        //还原文件
        RestoreFileContext restoreFileContext = new RestoreFileContext();
        restoreFileContext.setUserId(userId + 1);
        restoreFileContext.setFileIdList(Arrays.asList(fileId1, fileId2));
        recycleService.restore(restoreFileContext);


    }


    /**
     * 测试还原文件 失败 还原文件的名字已经在该文件的父目录中存在
     */

    @Test(expected = MPanBusinessException.class)
    public void testRestoreFileWithFileName02() {
        Long userId = userRegister();
        CurrentUserVO current = current(userId);

        //创建文件夹01
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(current.getRootFileId());
        Long fileId1 = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId1 != null && fileId1 > 0);


        //删除文件夹01
        DeleteFileWithRecycleContext deleteFileWithRecycleContext = new DeleteFileWithRecycleContext();
        deleteFileWithRecycleContext.setFileIdList(Collections.singletonList(fileId1));
        deleteFileWithRecycleContext.setUserId(userId);
        userFileService.deleteFileWithRecycle(deleteFileWithRecycleContext);

        //创建文件夹02
        createFolderContext.setFolderName("新建文件夹");
        Long fileId2 = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId2 != null && fileId2 > 0);

        //获取回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        Assert.assertTrue(voList != null && voList.size() == 1);

        //还原文件
        RestoreFileContext restoreFileContext = new RestoreFileContext();
        restoreFileContext.setUserId(userId + 1);
        restoreFileContext.setFileIdList(Collections.singletonList(fileId1));
        recycleService.restore(restoreFileContext);


    }






/*-------------------------------------------------------------------------private-------------------------------------------------------------------------*/
    private static final String USERNAME = "bobo";
    private static final String PASSWORD = "12345678";
    private static final String QUESTION = "question";
    private static final String ANSWER = "answer";

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
