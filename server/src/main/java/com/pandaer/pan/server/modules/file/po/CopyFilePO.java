package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("复制文件参数实体")
public class CopyFilePO {

    @ApiModelProperty("复制的文件Id列表")
    private List<String> copyFileIdList;

    @ApiModelProperty("目标文件夹Id")
    private String targetParentId;
}
