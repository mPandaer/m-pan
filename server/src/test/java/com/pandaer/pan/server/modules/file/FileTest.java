package com.pandaer.pan.server.modules.file;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.domain.MPanFile;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.enums.FileType;
import com.pandaer.pan.server.modules.file.service.IFileService;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.*;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.pandaer.pan.server.modules.user.UserTest.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FileTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IFileService fileService;

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


    //文件秒传成功
    @Test
    public void testSecFileUploadSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        String identifier = "identifier";

        MPanFile realFile = new MPanFile();
        realFile.setFileId(IdUtil.get());
        realFile.setFilename("filename");
        realFile.setRealPath("path");
        realFile.setFileSize("size");
        realFile.setFileSizeDesc("desc");
        realFile.setFileSuffix("suffix");
        realFile.setFilePreviewContentType("type");
        realFile.setIdentifier(identifier);
        realFile.setCreateTime(new Date());
        realFile.setCreateUser(userId);

        boolean success = fileService.save(realFile);
        Assert.assertTrue(success);

        SecFileUploadContext context = new SecFileUploadContext();
        context.setIdentifier(identifier);
        context.setFilename("filename_x");
        context.setUserId(userId);
        context.setParentId(currentUser.getRootFileId());
        userFileService.secFileUpload(context);

    }

    //文件秒传失败
    @Test(expected = MPanBusinessException.class)
    public void testSecFileUploadFail() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        String identifier = "identifier";

        SecFileUploadContext context = new SecFileUploadContext();
        context.setIdentifier(identifier);
        context.setFilename("filename_x");
        context.setUserId(userId);
        context.setParentId(currentUser.getRootFileId());
        userFileService.secFileUpload(context);
    }

    //单文件上传测试成功
    @Test
    public void testSingleFileUploadSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);


        //上传文件
        MultipartFile file = genMockFile();
        SingleFileUploadContext singleFileUploadContext = new SingleFileUploadContext();
        singleFileUploadContext.setUserId(userId);
        singleFileUploadContext.setFilename(file.getOriginalFilename());
        singleFileUploadContext.setParentId(currentUser.getRootFileId());
        singleFileUploadContext.setTotalSize(file.getSize());
        singleFileUploadContext.setIdentifier("identifier");
        singleFileUploadContext.setFileData(file);
        userFileService.singleFileUpload(singleFileUploadContext);

        QueryFileListContext context = new QueryFileListContext();
        context.setFileTypeList(null);
        context.setParentId(currentUser.getRootFileId());
        context.setDelFlag(FileConstants.NO);
        context.setUserId(userId);
        List<UserFileVO> list = userFileService.getFileList(context);
        Assert.assertTrue(list != null && list.size() == 1);


    }


    //测试获取上传分片文件的分片列表
    @Test
    public void testQueryUploadedFileChunkSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);


        //上传文件
        MultipartFile file = genMockFile();
        ChunkDataUploadContext chunkDataUploadContext = new ChunkDataUploadContext();
        chunkDataUploadContext.setCurrentChunkNumber(1);
        chunkDataUploadContext.setTotalChunks(1);
        chunkDataUploadContext.setFileData(file);
        chunkDataUploadContext.setCurrentChunkSize(file.getSize());
        chunkDataUploadContext.setTotalSize(file.getSize());
        chunkDataUploadContext.setIdentifier("identifier");
        chunkDataUploadContext.setFilename(file.getOriginalFilename());
        chunkDataUploadContext.setUserId(userId);
        ChunkDataUploadVO vo = userFileService.chunkDataUpload(chunkDataUploadContext);
        Assert.assertTrue(vo.getChunkNumber() == 1 && vo.getMerge() == 1);

        //获取文件分片列表
        QueryUploadedFileChunkContext queryUploadedFileChunkContext = new QueryUploadedFileChunkContext();
        queryUploadedFileChunkContext.setIdentifier("identifier");
        queryUploadedFileChunkContext.setUserId(userId);
        UploadedFileChunkVO uploadedFileChunkVO = userFileService.queryUploadedFileChunk(queryUploadedFileChunkContext);
        Assert.assertEquals(1, uploadedFileChunkVO.getUploadedChunkNumberList().size());
    }


    //测试多线程文件分片上传
    @Test
    public void testChunkFileUpload() throws InterruptedException {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new ChunkFileUploader(currentUser.getRootFileId(),
                    userId,i + 1,10,userFileService,countDownLatch).start();
        }

        countDownLatch.await();
    }


    //定义一个上传器
    @AllArgsConstructor
    static class ChunkFileUploader extends Thread {
        private Long parentId;

        private Long userId;

        private Integer chunkNumber;

        private int chunks;

        private IUserFileService userFileService;


        private CountDownLatch countDownLatch;



        @Override
        public void run() {
            MultipartFile chunkFile = genMockFile();
            String identifier = "123456789";
            String filename = "demo.txt";
            ChunkDataUploadContext chunkDataUploadContext = new ChunkDataUploadContext();
            chunkDataUploadContext.setUserId(userId);
            chunkDataUploadContext.setTotalChunks(chunks);
            chunkDataUploadContext.setFileData(chunkFile);
            chunkDataUploadContext.setIdentifier(identifier);
            chunkDataUploadContext.setFilename(filename);
            chunkDataUploadContext.setTotalSize(chunkFile.getSize() * chunks);
            chunkDataUploadContext.setCurrentChunkSize(chunkFile.getSize());
            chunkDataUploadContext.setCurrentChunkNumber(chunkNumber);
            ChunkDataUploadVO vo = userFileService.chunkDataUpload(chunkDataUploadContext);
            if (Objects.equals(vo.getMerge(), FileConstants.YES)) {
                System.out.println(vo.getChunkNumber() + "号分片 检测到需要合并文件 " + vo.getMerge());
                MergeChunkFileContext mergeChunkFileContext = new MergeChunkFileContext();
                mergeChunkFileContext.setIdentifier(identifier);
                mergeChunkFileContext.setFilename(filename);
                mergeChunkFileContext.setParentId(parentId);
                mergeChunkFileContext.setUserId(userId);
                mergeChunkFileContext.setTotalChunks((long) chunks);
                mergeChunkFileContext.setTotalSize(chunkFile.getSize() * chunks);
                userFileService.mergeChunkFile(mergeChunkFileContext);
            }

            countDownLatch.countDown();

        }
    }


    //测试获取文件夹树
    @Test
    public void testGetFolderTree() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);


        createFolderContext.setFolderName("新建文件夹-2");
        fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);


        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-2-1");
        createFolderContext.setParentId(fileId);
        fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);


        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.assertTrue(folderTree != null && folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }



    //测试批量移动文件成功
    @Test
    public void testMoveFileSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long parentFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(parentFolderId != null && parentFolderId > 0);


        //创建子文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1-1");
        createFolderContext.setParentId(parentFolderId);
        Long childFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(childFolderId != null && childFolderId > 0);


        //增加一个用户文件记录
        MPanUserFile userFile = new MPanUserFile();
        Long fileId = IdUtil.get();
        userFile.setFileId(fileId);
        userFile.setUserId(userId);
        userFile.setParentId(childFolderId);
        userFile.setRealFileId(-1L);
        userFile.setFilename("demo.txt");
        userFile.setFolderFlag(FileConstants.NO);
        userFile.setFileSizeDesc("");
        userFile.setFileType(FileType.getFileTypeCode(".txt"));
        userFile.setDelFlag(FileConstants.NO);
        userFile.setCreateUser(userId);
        userFile.setCreateTime(new Date());
        userFile.setUpdateUser(userId);
        userFile.setUpdateTime(new Date());
        userFileService.save(userFile);

        //创建目标文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-2");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long targetFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(targetFolderId != null && targetFolderId > 0);
        //移动文件
        MoveFileContext moveFileContext = new MoveFileContext();
        moveFileContext.setTargetParentId(targetFolderId);
        moveFileContext.setUserId(userId);
        moveFileContext.setFileIdList(Collections.singletonList(fileId));
        userFileService.moveFile(moveFileContext);

    }

    //批量移动文件失败 -- 移动的文件中存在目标文件夹或者其子文件夹
    @Test(expected = MPanBusinessException.class)
    public void testMoveFileFailTargetFolderInFile() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long parentFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(parentFolderId != null && parentFolderId > 0);


        //创建子文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1-1");
        createFolderContext.setParentId(parentFolderId);
        Long childFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(childFolderId != null && childFolderId > 0);


        //增加一个用户文件记录
        MPanUserFile userFile = new MPanUserFile();
        Long fileId = IdUtil.get();
        userFile.setFileId(fileId);
        userFile.setUserId(userId);
        userFile.setParentId(childFolderId);
        userFile.setRealFileId(-1L);
        userFile.setFilename("demo.txt");
        userFile.setFolderFlag(FileConstants.NO);
        userFile.setFileSizeDesc("");
        userFile.setFileType(FileType.getFileTypeCode(".txt"));
        userFile.setDelFlag(FileConstants.NO);
        userFile.setCreateUser(userId);
        userFile.setCreateTime(new Date());
        userFile.setUpdateUser(userId);
        userFile.setUpdateTime(new Date());
        userFileService.save(userFile);

        //创建目标文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-2");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long targetFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(targetFolderId != null && targetFolderId > 0);
        //移动文件
        MoveFileContext moveFileContext = new MoveFileContext();
        moveFileContext.setTargetParentId(childFolderId);
        moveFileContext.setUserId(userId);
        moveFileContext.setFileIdList(Collections.singletonList(parentFolderId));
        userFileService.moveFile(moveFileContext);

    }



    //测试批量移动文件成功
    @Test
    public void testCopyFileSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long parentFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(parentFolderId != null && parentFolderId > 0);


        //创建子文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1-1");
        createFolderContext.setParentId(parentFolderId);
        Long childFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(childFolderId != null && childFolderId > 0);


        //增加一个用户文件记录
        MPanUserFile userFile = new MPanUserFile();
        Long fileId = IdUtil.get();
        userFile.setFileId(fileId);
        userFile.setUserId(userId);
        userFile.setParentId(childFolderId);
        userFile.setRealFileId(-1L);
        userFile.setFilename("demo.txt");
        userFile.setFolderFlag(FileConstants.NO);
        userFile.setFileSizeDesc("");
        userFile.setFileType(FileType.getFileTypeCode(".txt"));
        userFile.setDelFlag(FileConstants.NO);
        userFile.setCreateUser(userId);
        userFile.setCreateTime(new Date());
        userFile.setUpdateUser(userId);
        userFile.setUpdateTime(new Date());
        userFileService.save(userFile);

        //创建目标文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-2");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long targetFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(targetFolderId != null && targetFolderId > 0);
        //复制文件
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setTargetParentId(targetFolderId);
        copyFileContext.setUserId(userId);
        copyFileContext.setCopyFileIdList(Collections.singletonList(fileId));
        userFileService.copyFile(copyFileContext);

    }

    //批量移动文件失败 -- 移动的文件中存在目标文件夹或者其子文件夹
    @Test(expected = MPanBusinessException.class)
    public void testCopyFileFailTargetFolderInFile() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long parentFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(parentFolderId != null && parentFolderId > 0);


        //创建子文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1-1");
        createFolderContext.setParentId(parentFolderId);
        Long childFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(childFolderId != null && childFolderId > 0);


        //增加一个用户文件记录
        MPanUserFile userFile = new MPanUserFile();
        Long fileId = IdUtil.get();
        userFile.setFileId(fileId);
        userFile.setUserId(userId);
        userFile.setParentId(childFolderId);
        userFile.setRealFileId(-1L);
        userFile.setFilename("demo.txt");
        userFile.setFolderFlag(FileConstants.NO);
        userFile.setFileSizeDesc("");
        userFile.setFileType(FileType.getFileTypeCode(".txt"));
        userFile.setDelFlag(FileConstants.NO);
        userFile.setCreateUser(userId);
        userFile.setCreateTime(new Date());
        userFile.setUpdateUser(userId);
        userFile.setUpdateTime(new Date());
        userFileService.save(userFile);

        //创建目标文件夹
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-2");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long targetFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(targetFolderId != null && targetFolderId > 0);
        //复制文件
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setTargetParentId(childFolderId);
        copyFileContext.setUserId(userId);
        copyFileContext.setCopyFileIdList(Collections.singletonList(parentFolderId));
        userFileService.copyFile(copyFileContext);

    }

    //测试文件搜索
    @Test
    public void testSearchFile() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹-1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long parentFolderId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(parentFolderId != null && parentFolderId > 0);

        SearchFileContext searchFileContext = new SearchFileContext();
        searchFileContext.setKeyword("新建");
        searchFileContext.setUserId(userId);
        List<SearchFileInfoVO> voList = userFileService.searchFile(searchFileContext);
        Assert.assertTrue(voList != null && voList.size() == 1);

        searchFileContext = new SearchFileContext();
        searchFileContext.setKeyword("文件夹");
        searchFileContext.setUserId(userId);
        voList = userFileService.searchFile(searchFileContext);
        Assert.assertTrue(voList != null && voList.isEmpty());
    }









    private static MultipartFile genMockFile() {
        return new MockMultipartFile("file","demo.txt","multipart/form-data","demo test"
                .getBytes(StandardCharsets.UTF_8));
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
