package com.pandaer.pan.server.modules.file.controller;


import com.google.common.base.Splitter;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.*;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.po.*;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.ChunkDataUploadVO;
import com.pandaer.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.pandaer.pan.server.modules.file.vo.UploadedFileChunkVO;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        Long decParentId = IdUtil.decrypt(parentId);
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
    public Resp<ChunkDataUploadVO> ChunkDataUpload(@Validated @RequestBody ChunkDataUploadPO chunkDataUploadPO) {
        ChunkDataUploadContext context = fileConverter.PO2ContextInChunkDataUpload(chunkDataUploadPO);
        ChunkDataUploadVO vo = userFileService.chunkDataUpload(context);
        return Resp.successAndData(vo);
    }


    @ApiOperation(value = "获取已经上传的文件分片列表",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("/file/chunk-upload")
    public Resp<UploadedFileChunkVO> queryUploadedFileChunk(@Validated @RequestBody QueryUploadedFileChunkPO queryUploadedFileChunkPO) {
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
        fileDownloadContext.setFileId(fileId);
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
        filePreviewContext.setFileId(fileId);
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





}































