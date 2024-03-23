package com.pandaer.pan.server.modules.file.controller;


import com.google.common.base.Splitter;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.po.*;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api("文件模块")
@Validated
public class FileController {

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private FileConverter fileConverter;


    @ApiOperation(value = "查询文件列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("files")
    public Resp<List<UserFileVO>> list(
            @NotBlank(message = "父文件夹Id不能为空")
            @RequestParam(value = "parentId")
                String parentId,
            @RequestParam(value = "fileTypes", defaultValue = FileConstants.ALL_FILE_TYPE,required = false)
                String fileTypes
    ) {
        Long decParentId = -1L;
        if (!"-1".equals(parentId)) {
            decParentId = IdUtil.decrypt(parentId);
        }
        List<Integer> fileTypeList = null;
        if (!StringUtils.equals(fileTypes,FileConstants.ALL_FILE_TYPE)) {
            fileTypeList = Splitter.on(MPanConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream()
                    .map(Integer::valueOf).collect(Collectors.toList());
        }
        QueryFileListContext context = fileConverter
                .genContextInQueryFileList(decParentId,fileTypeList, UserIdUtil.getUserId(),FileConstants.NO);

        List<UserFileVO> list = userFileService.getFileList(context);
        return Resp.successAndData(list);
    }

    @ApiOperation(value = "创建文件夹",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("file/folder")
    public Resp<String> createFolder(@Validated @RequestBody CreateFolderPO createFolderPO) {
        CreateFolderContext context = fileConverter.PO2ContextInCreateFolder(createFolderPO);
        Long fileId = userFileService.creatFolder(context);
        return Resp.successAndData(IdUtil.encrypt(fileId));
    }


    @ApiOperation(value = "文件重命名",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PutMapping("file")
    public Resp<Object> updateFileName(@Validated @RequestBody UpdateFilenamePO updateFilenamePO) {
        UpdateFilenameContext context = fileConverter.PO2ContextInUpdateFilename(updateFilenamePO);
        userFileService.updateFilename(context);
        return Resp.success();
    }

    @ApiOperation(value = "批量删除文件到回收站",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DeleteMapping("file")
    public Resp<Object> deleteFileWithRecycle(@Validated @RequestBody DeleteFileWithRecyclePO deleteFileWithRecyclePO) {
        DeleteFileWithRecycleContext context = fileConverter.PO2ContextInDeleteFileWithRecycle(deleteFileWithRecyclePO);
        userFileService.deleteFileWithRecycle(context);
        return Resp.success();
    }

    @ApiOperation(value = "文件秒传",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("file/sec-upload")
    public Resp<Boolean> secFileUpload(@Validated @RequestBody SecFileUploadPO secFileUploadPO) {
        SecFileUploadContext context = fileConverter.PO2ContextInSecFileUpload(secFileUploadPO);
        boolean success = userFileService.secFileUpload(context);
        return Resp.successAndData(success);
    }

    @ApiOperation(value = "单文件上传",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("/file/upload")
    public Resp<Object> singleFileUpload(@Validated @RequestBody SingleFileUploadPO singleFileUploadPO) {
        SingleFileUploadContext context = fileConverter.PO2ContextInSingleFileUpload(singleFileUploadPO);
        userFileService.singleFileUpload(context);
        return Resp.success();
    }

    @ApiOperation(value = "上传分片数据",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("/file/chunk-upload")
    public Resp<ChunkDataUploadVO> ChunkDataUpload(@Validated ChunkDataUploadPO chunkDataUploadPO) {
        ChunkDataUploadContext context = fileConverter.PO2ContextInChunkDataUpload(chunkDataUploadPO);
        ChunkDataUploadVO vo = userFileService.chunkDataUpload(context);
        return Resp.successAndData(vo);
    }


    @ApiOperation(value = "获取已经上传的文件分片列表",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("/file/chunk-upload")
    public Resp<UploadedFileChunkVO> queryUploadedFileChunk(@Validated QueryUploadedFileChunkPO queryUploadedFileChunkPO) {
        QueryUploadedFileChunkContext context = fileConverter.PO2ContextInQueryUploadedFileChunk(queryUploadedFileChunkPO);
        UploadedFileChunkVO vo = userFileService.queryUploadedFileChunk(context);
        return Resp.successAndData(vo);
    }


    @ApiOperation(value = "执行合并操作",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("/file/merge")
    public Resp<Object> mergeChunkFile(@Validated @RequestBody MergeChunkFilePO mergeChunkFilePO) {
        MergeChunkFileContext context = fileConverter.PO2ContextInMergeChunkFile(mergeChunkFilePO);
        userFileService.mergeChunkFile(context);
        return Resp.success();
    }


    @ApiOperation(value = "文件下载",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @GetMapping("/file/download")
    public void mergeChunkFile(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId") String fileId, HttpServletResponse response) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(IdUtil.decrypt(fileId));
        fileDownloadContext.setResponse(response);
        fileDownloadContext.setUserId(UserIdUtil.getUserId());
        userFileService.download(fileDownloadContext);

    }

    @ApiOperation(value = "文件预览",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @GetMapping("/file/preview")
    public void preview(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId") String fileId, HttpServletResponse response) {
        FilePreviewContext filePreviewContext = new FilePreviewContext();
        filePreviewContext.setFileId(IdUtil.decrypt(fileId));
        filePreviewContext.setResponse(response);
        filePreviewContext.setUserId(UserIdUtil.getUserId());
        userFileService.preview(filePreviewContext);

    }

    @ApiOperation(value = "获取文件夹树",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("/file/folder/tree")
    public Resp<List<FolderTreeNodeVO>> getFolderTree() {
        QueryFolderTreeContext filePreviewContext = new QueryFolderTreeContext();
        filePreviewContext.setUserId(UserIdUtil.getUserId());
        List<FolderTreeNodeVO> list =  userFileService.getFolderTree(filePreviewContext);
        return Resp.successAndData(list);
    }


    @ApiOperation(value = "文件批量移动",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("/file/move")
    public Resp<Object> moveFile(@Validated @RequestBody MoveFilePO moveFilePO) {
        MoveFileContext moveFileContext = fileConverter.PO2ContextInMoveFile(moveFilePO);
        userFileService.moveFile(moveFileContext);
        return Resp.success();
    }

    @ApiOperation(value = "文件批量复制",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("/file/copy")
    public Resp<Object> copyFile(@Validated @RequestBody CopyFilePO copyFilePO) {
        CopyFileContext copyFileContext = fileConverter.PO2ContextInCopyFile(copyFilePO);
        userFileService.copyFile(copyFileContext);
        return Resp.success();
    }

    @ApiOperation(value = "文件搜索",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("/file/search")
    public Resp<List<SearchFileInfoVO>> copyFile(@Validated SearchFilePO searchFilePO){
        SearchFileContext searchFileContext = fileConverter.PO2ContextInSearchFile(searchFilePO);
        String fileType = searchFilePO.getFileType();
        if (StringUtils.isNotBlank(fileType)) {
            List<Integer> fileTypeList = Splitter.on(MPanConstants.COMMON_SEPARATOR).splitToList(fileType).stream()
                    .map(Integer::valueOf).collect(Collectors.toList());
            searchFileContext.setFileTypeList(fileTypeList);
        }
        List<SearchFileInfoVO> voList = userFileService.searchFile(searchFileContext);
        return Resp.successAndData(voList);
    }

    @ApiOperation(value = "文件面包屑导航",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("/file/breadcrumb")
    public Resp<List<BreadcrumbVO>> getBreadcrumb(@NotBlank(message = "文件ID不能为空") String fileId){
        BreadcrumbContext breadcrumbContext = fileConverter.params2ContextInGetBreadcrumb(fileId);
        List<BreadcrumbVO> breadcrumb = userFileService.getBreadcrumb(breadcrumbContext);
        return Resp.successAndData(breadcrumb);
    }





}































