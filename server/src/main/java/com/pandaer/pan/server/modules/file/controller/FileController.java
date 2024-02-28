package com.pandaer.pan.server.modules.file.controller;


import com.google.common.base.Splitter;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.context.DeleteFileWithRecycleContext;
import com.pandaer.pan.server.modules.file.context.QueryFileListContext;
import com.pandaer.pan.server.modules.file.context.UpdateFilenameContext;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.po.CreateFolderPO;
import com.pandaer.pan.server.modules.file.po.DeleteFileWithRecyclePO;
import com.pandaer.pan.server.modules.file.po.UpdateFilenamePO;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

}































