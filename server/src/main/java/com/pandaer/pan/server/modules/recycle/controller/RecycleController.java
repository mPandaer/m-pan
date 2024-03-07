package com.pandaer.pan.server.modules.recycle.controller;


import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.pandaer.pan.server.modules.recycle.context.RestoreFileContext;
import com.pandaer.pan.server.modules.recycle.po.RestoreFilePO;
import com.pandaer.pan.server.modules.recycle.service.IRecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@Validated
@Api("回收站模块")
public class RecycleController {

    @Autowired
    private IRecycleService recycleService;

    @Autowired
    private FileConverter fileConverter;


    @ApiOperation(
            value = "获取回收站列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("recycles")
    public Resp<List<UserFileVO>> recycles() {
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(UserIdUtil.getUserId());
        List<UserFileVO> voList = recycleService.queryRecycleFileList(queryRecycleFileListContext);
        return Resp.successAndData(voList);
    }


    @ApiOperation(
            value = "还原文件",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PutMapping("recycle/restore")
    public Resp<Object> restore(@Validated @RequestBody RestoreFilePO restoreFilePO) {
        RestoreFileContext restoreFileContext = fileConverter.PO2ContextInRestoreFile(restoreFilePO);
        recycleService.restore(restoreFileContext);
        return Resp.success();
    }

}
